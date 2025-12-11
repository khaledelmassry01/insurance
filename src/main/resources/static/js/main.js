let currentType = '';
let currentColumns = [];

// Stats caching system
const statsCache = {
  data: null,
  timestamp: null,
  // Cache expiration time in milliseconds (5 minutes)
  expirationTime: 5 * 60 * 1000,
  
  // Check if cache is valid
  isValid() {
    return this.data && this.timestamp && (Date.now() - this.timestamp < this.expirationTime);
  },
  
  // Store data in cache
  store(data) {
    this.data = data;
    this.timestamp = Date.now();
    return data;
  },
  
  // Clear cache
  clear() {
    this.data = null;
    this.timestamp = null;
  }
};

// Centralized function to get dashboard stats
async function getDashboardStats(forceRefresh = false) {
  // Return cached data if valid and no force refresh requested
  if (!forceRefresh && statsCache.isValid()) {
    console.log('Using cached stats data');
    return statsCache.data;
  }
  
  try {
    console.log('Fetching fresh stats data');
    const stats = await apiRequest('/api/dashboard/stats');
    return statsCache.store(stats);
  } catch (error) {
    console.error('Error fetching dashboard stats:', error);
    throw error;
  }
}

// Expose the function to the global scope so other scripts can use it
window.getDashboardStats = getDashboardStats;

function authHeaders() {
    const token = localStorage.getItem('token');
    return token
        ? { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }
        : { 'Content-Type': 'application/json' };
}

document.addEventListener('DOMContentLoaded', () => {
    const page = window.location.pathname.split('/').pop();
    switch (page) {
        case 'policyholders.html':
            loadPolicyholders();
            break;
        case 'policies.html':
            loadPolicies();
            console.log('Policies page loaded - initializing selection system');
            break;
        case 'reports.html':
            console.log('Reports page loaded');
            const policyholderBtn = document.getElementById('btn-policyholder-analytics');
            const policyPerfBtn = document.getElementById('btn-policy-performance');
            const financialBtn = document.getElementById('btn-financial-reports');
            const analyticsContent = document.getElementById('analytics-overview-content');
            const policyStatsDiv = document.getElementById('policy-performance-stats');
            const filterBtn = document.getElementById('analytics-filter-btn');
            const startDateInput = document.getElementById('analytics-start-date');
            const endDateInput = document.getElementById('analytics-end-date');

            let currentAnalytics = null;

            function buildQuery() {
                const params = [];
                if (startDateInput.value) params.push(`start=${startDateInput.value}`);
                if (endDateInput.value) params.push(`end=${endDateInput.value}`);
                return params.length ? '?' + params.join('&') : '';
            }

            if (policyholderBtn) {
                policyholderBtn.onclick = async () => {
                    currentAnalytics = 'policyholder';
                    await loadPolicyholderAnalytics();
                };
            }
            if (policyPerfBtn) {
                policyPerfBtn.onclick = async () => {
                    currentAnalytics = 'policy';
                    await loadPolicyPerformanceAnalytics();
                };
            }
            if (financialBtn) {
                financialBtn.onclick = async () => {
                    currentAnalytics = 'payment';
                    await loadFinancialAnalytics();
                };
            }
            if (filterBtn) {
                filterBtn.onclick = async () => {
                    if (currentAnalytics === 'policyholder') await loadPolicyholderAnalytics();
                    else if (currentAnalytics === 'policy') await loadPolicyPerformanceAnalytics();
                    else if (currentAnalytics === 'payment') await loadFinancialAnalytics();
                };
            }

            async function loadPolicyholderAnalytics() {
                try {
                    const query = buildQuery();
                    const data = await apiRequest(`/api/reports/policyholder-analytics${query}`);
                    currentType = 'policyholder';
                    currentColumns = [
                        { header: 'ID', key: 'policyholderId' },
                        { header: 'Name', key: 'fullName' },
                        { header: 'Email', key: 'email' },
                        { header: 'Phone', key: 'phoneNumber' },
                        { header: 'Enrollment Date', key: 'enrollmentDate' }
                    ];
                    const tableData = data.policyholders.map(ph => ({
                        ...ph,
                        fullName: `${ph.firstName} ${ph.lastName}`,
                        enrollmentDate: ph.enrollmentDate ? formatDate(ph.enrollmentDate) : ''
                    }));
                    analyticsContent.innerHTML = renderTable(tableData, currentColumns, currentType);
                    analyticsContent.style.display = 'block';
                    if (policyStatsDiv) policyStatsDiv.style.display = 'none';
                } catch (err) {
                    console.error('Error loading policyholder analytics:', err);
                    analyticsContent.innerHTML = '<p class="text-danger">Failed to load policyholder analytics.</p>';
                    analyticsContent.style.display = 'block';
                    if (policyStatsDiv) policyStatsDiv.style.display = 'none';
                }
            }

            async function loadPolicyPerformanceAnalytics() {
                try {
                    const query = buildQuery();
                    const data = await apiRequest(`/api/reports/policy-performance${query}`);
                    let policyList = null;
                    if (data && data.items && Array.isArray(data.items)) {
                        policyList = data.items;
                    } else if (data && data.policies && Array.isArray(data.policies)) {
                        policyList = data.policies;
                    } else if (data && data.policyList && Array.isArray(data.policyList)) {
                        policyList = data.policyList;
                    } else if (Array.isArray(data)) {
                        policyList = data;
                    }
                    if (!policyList) {
                        console.error('Could not find policy list in API response. Checked keys: items, policies, policyList, or root array.', data);
                        throw new Error('Invalid data structure from API');
                    }
                    currentType = 'policy';
                    currentColumns = [
                        { header: 'ID', key: 'insuranceId' },
                        { header: 'Type', key: 'policyType' },
                        { header: 'Status', key: 'status' },
                        { header: 'End Date', key: 'expirationDate' }
                    ];
                    const tableData = policyList.map(p => ({
                        ...p,
                        expirationDate: formatDate(p.expirationDate)
                    }));
                    analyticsContent.innerHTML = renderTable(tableData, currentColumns, currentType);
                    analyticsContent.style.display = 'block';
                    if (policyStatsDiv) policyStatsDiv.style.display = 'none';
                } catch (err) {
                    console.error('Error loading policy performance table:', err);
                    analyticsContent.innerHTML = '<p class="text-danger">Failed to load policy performance data.</p>';
                    analyticsContent.style.display = 'block';
                    if (policyStatsDiv) policyStatsDiv.style.display = 'none';
                }
            }

            async function loadFinancialAnalytics() {
                try {
                    const query = buildQuery();
                    const data = await apiRequest(`/api/reports/financial${query}`);
                    currentType = 'payment';
                    currentColumns = [
                        { header: 'Payment ID', key: 'paymentId' },
                        { header: 'Insurance ID', key: 'insuranceId' },
                        { header: 'Amount', key: 'amount' },
                        { header: 'Installment #', key: 'installmentNumber' },
                        { header: 'Due Date', key: 'dueDate' },
                        { header: 'Payment Date', key: 'paymentDate' },
                        { header: 'Payment Method', key: 'paymentMethod' },
                        { header: 'Status', key: 'latestStatus' }
                    ];
                    const tableData = data.payments.map(p => ({
                        ...p,
                        dueDate: formatDate(p.dueDate),
                        paymentDate: formatDate(p.paymentDate),
                        amount: p.amount ? `$${parseFloat(p.amount).toFixed(2)}` : 'N/A',
                        insuranceId: p.insuranceId || 'N/A',
                        latestStatus: p.latestStatus || 'N/A',
                        paymentMethod: p.paymentMethod || 'N/A',
                        installmentNumber: p.installmentNumber || 'N/A'
                    }));
                    
                    // Build summary cards HTML
                    const summaryHTML = `
                        <div class="row mb-4">
                            <div class="col-md-4">
                                <div class="card text-center">
                                    <div class="card-body">
                                        <h5 class="card-title text-muted">Total Payments</h5>
                                        <p class="card-text fs-3 fw-bold text-primary">${data.payments.length}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-center">
                                    <div class="card-body">
                                        <h5 class="card-title text-muted">Total Premium</h5>
                                        <p class="card-text fs-3 fw-bold text-success">$${data.totalPremium ? parseFloat(data.totalPremium).toFixed(2) : '0.00'}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card text-center">
                                    <div class="card-body">
                                        <h5 class="card-title text-muted">Total Claims</h5>
                                        <p class="card-text fs-3 fw-bold text-warning">${data.totalClaims || 0}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                    
                    analyticsContent.innerHTML = summaryHTML + renderTable(tableData, currentColumns, currentType);
                    analyticsContent.style.display = 'block';
                    if (policyStatsDiv) policyStatsDiv.style.display = 'none';
                } catch (err) {
                    console.error('Error loading financial reports:', err);
                    analyticsContent.innerHTML = '<p class="text-danger">Failed to load financial reports.</p>';
                    analyticsContent.style.display = 'block';
                    if (policyStatsDiv) policyStatsDiv.style.display = 'none';
                }
            }
            break;
        default:
            // if you have a dashboard.html or index.html
            if (page === 'dashboard.html' || page === 'index.html') {
                loadDashboard();
            }
    }

    // Policyholder search filter
    const searchInput = document.querySelector('input[placeholder="Search policyholders..."]');
    if (searchInput) {
        searchInput.addEventListener('input', async function () {
            const query = this.value.trim();
            const list = await apiRequest('/api/policyholders');
            let filtered = list;
            if (query) {
                filtered = list.filter(ph => ph.policyholderId && ph.policyholderId.toString().includes(query));
            }
            const tbody = document.getElementById('policyholders-table-body');
            if (tbody) {
                tbody.innerHTML = filtered.map(ph => `
                    <tr>
                        <td>${ph.policyholderId}</td>
                        <td>${ph.firstName} ${ph.lastName}</td>
                        <td>${ph.email}</td>
                        <td>${ph.phoneNumber}</td>
                        <td>${ph.status ? `<span class="badge bg-${badgeClass(ph.status)}">${ph.status}</span>` : '-'}</td>
                        <td>
                            <button class="btn btn-sm btn-primary" onclick="editPolicyholder(${ph.policyholderId})">Edit</button>
                            <button class="btn btn-sm btn-danger" onclick="deletePolicyholder(${ph.policyholderId})">Delete</button>
                        </td>
                    </tr>
                `).join('');
            }
        });
    }

    // Policies search filter (by ID only)
    const policySearchInput = document.querySelector('input[placeholder="Search policies by ID..."]');
    if (policySearchInput) {
        policySearchInput.addEventListener('input', async function () {
            const query = this.value.trim();
            const list = await apiRequest('/api/policies');
            let filtered = list;
            if (query) {
                filtered = list.filter(p => p.insuranceId && p.insuranceId.toString().includes(query));
            }
            const tbody = document.getElementById('policies-table-body');
            if (tbody) {
                tbody.innerHTML = filtered.map(policy => `
                    <tr>
                        <td>${policy.insuranceId}</td>
                        <td>${policy.policyType || 'N/A'}</td>
                        <td>${policy.policyholderName || 'N/A'}</td>
                        <td>
                            <span class="badge bg-${badgeClass(policy.status || 'Unknown')}">
                                ${policy.status || 'Unknown'}
                            </span>
                        </td>
                        <td>${formatDate(policy.expirationDate)}</td>
                        <td>
                            <button class="btn btn-sm btn-primary" onclick="editPolicy(${policy.insuranceId})">
                                Edit
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="deletePolicy(${policy.insuranceId})">
                                Delete
                            </button>
                        </td>
                    </tr>
                `).join('');
            }
        });
    }
});

async function loadPolicies() {
    try {
        const list = await apiRequest('/api/policies');
        
        const tbody = document.getElementById('policies-table-body');
        if (!tbody) return;

        tbody.innerHTML = list.map(policy => `
            <tr data-policy-id="${policy.insuranceId}">
                <td>
                    <input type="checkbox" class="form-check-input policy-checkbox" data-policy-id="${policy.insuranceId}">
                </td>
                <td>${policy.insuranceId}</td>
                <td>${policy.policyType || 'N/A'}</td>
                <td>${policy.policyholderName || 'N/A'}</td>
                <td>
                    <span class="badge bg-${badgeClass(policy.status || 'Unknown')}">
                        ${policy.status || 'Unknown'}
                    </span>
                </td>
                <td>${formatDate(policy.expirationDate)}</td>
            </tr>
        `).join('');

        // Update dashboard stats if available
        const totalPolicies = document.getElementById('total-policies'); // Corrected ID
        const activePolicies = document.getElementById('active-policies-count'); // Correct ID
        const pendingPolicies = document.getElementById('pending-policies-count'); // Correct ID
        const expiredPolicies = document.getElementById('expired-policies'); // Corrected ID

        if (totalPolicies) totalPolicies.textContent = list.length;
        if (activePolicies) activePolicies.textContent = list.filter(p => p.status === 'Active').length;
        if (pendingPolicies) pendingPolicies.textContent = list.filter(p => p.status === 'Pending').length; 
        if (expiredPolicies) expiredPolicies.textContent = list.filter(p => p.status === 'Expired').length;

        // Set up policy selection functionality
        setTimeout(() => setupPolicySelection(), 100); // Add a small delay to ensure DOM is updated

    } catch (err) {
        console.error('Error loading policies:', err);
        const tbody = document.getElementById('policies-table-body');
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-danger">
                        Failed to load policies. Please try again later.
                    </td>
                </tr>
            `;
        }
    }
}

// Add new function for policy selection functionality
function setupPolicySelection() {
    console.log('Setting up policy selection...');
    const selectAllCheckbox = document.getElementById('select-all-policies');
    const policyCheckboxes = document.querySelectorAll('.policy-checkbox');
    const bulkEditBtn = document.getElementById('bulk-edit-btn');
    const bulkDeleteBtn = document.getElementById('bulk-delete-btn');
    
    console.log('Elements found:', {
        selectAllCheckbox: !!selectAllCheckbox,
        policyCheckboxes: policyCheckboxes.length,
        bulkEditBtn: !!bulkEditBtn,
        bulkDeleteBtn: !!bulkDeleteBtn
    });
    
    if (!selectAllCheckbox || !bulkEditBtn || !bulkDeleteBtn) {
        console.error('Missing required elements for policy selection');
        return;
    }
    
    // Select all checkbox functionality
    selectAllCheckbox.addEventListener('change', function() {
        policyCheckboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        });
    });
    
    // Bulk edit button
    bulkEditBtn.addEventListener('click', function() {
        const selectedIds = Array.from(document.querySelectorAll('.policy-checkbox:checked'))
            .map(checkbox => checkbox.getAttribute('data-policy-id'));
        
        if (selectedIds.length === 0) {
            alert('Please select at least one policy to edit.');
            return;
        }
        
        if (selectedIds.length === 1) {
            // If only one policy is selected, use the existing edit function
            editPolicy(selectedIds[0]);
        } else if (selectedIds.length > 1) {
            // For multiple selections, we could implement a bulk edit modal
            alert('Bulk editing of multiple policies is not yet implemented.');
            // Future enhancement: implement bulk edit functionality
        }
    });
    
    // Bulk delete button
    bulkDeleteBtn.addEventListener('click', function() {
        const selectedIds = Array.from(document.querySelectorAll('.policy-checkbox:checked'))
            .map(checkbox => checkbox.getAttribute('data-policy-id'));
        
        if (selectedIds.length === 0) {
            alert('Please select at least one policy to delete.');
            return;
        }
        
        showConfirmNotification(
            `Are you sure you want to delete ${selectedIds.length} selected policies?`, 
            () => {
                // Delete each selected policy
                Promise.all(selectedIds.map(id => 
                    apiRequest(`/api/policies/${id}`, 'DELETE')
                ))
                .then(() => {
                    // Reload the policies list after successful deletion
                    loadPolicies();
                    showNotification('success', `Successfully deleted ${selectedIds.length} policies.`);
                })
                .catch(err => {
                    console.error('Error deleting policies:', err);
                    showNotification('error', 'Failed to delete some policies. Please try again.');
                });
            }
        );
    });
}

async function loadPolicyholders() {
    try {
        const list = await apiRequest('/api/policyholders');
        
        const tbody = document.getElementById('policyholders-table-body');
        tbody.innerHTML = list.map(ph => `
            <tr data-policyholder-id="${ph.policyholderId}">
                <td>
                    <input type="checkbox" class="form-check-input policyholder-checkbox" data-policyholder-id="${ph.policyholderId}">
                </td>
                <td>${ph.policyholderId}</td>
                <td>${ph.firstName} ${ph.lastName}</td>
                <td>${ph.email}</td>
                <td>${ph.phoneNumber}</td>
                <td>${ph.status ? `<span class="badge bg-${badgeClass(ph.status)}">${ph.status}</span>` : '-'}</td>
            </tr>
        `).join('');

        // Update dashboard stats if available
        const totalPolicyholders = document.getElementById('total-policyholders');
        const activePolicies = document.getElementById('active-policies');
        const newThisMonth = document.getElementById('new-this-month');
        const pendingActions = document.getElementById('pending-actions');

        if (totalPolicyholders) totalPolicyholders.textContent = list.length;
        if (activePolicies) activePolicies.textContent = list.filter(ph => ph.status === 'Active').length;
        if (newThisMonth) {
            const now = new Date();
            newThisMonth.textContent = list.filter(ph => {
                if (!ph.enrollmentDate) return false;
                const d = new Date(ph.enrollmentDate);
                return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
            }).length;
        }
        if (pendingActions) pendingActions.textContent = list.filter(ph => ph.status === 'Pending').length;
        
        // Set up policyholder selection functionality
        setTimeout(() => setupPolicyholderSelection(), 100); // Add a small delay to ensure DOM is updated
        
    } catch (err) {
        console.error('Error loading policyholders:', err);
        showError('Failed to load policyholders.');
    }
}

// Add function for policyholder selection functionality
function setupPolicyholderSelection() {
    console.log('Setting up policyholder selection...');
    const selectAllCheckbox = document.getElementById('select-all-policyholders');
    const policyholderCheckboxes = document.querySelectorAll('.policyholder-checkbox');
    const bulkEditBtn = document.getElementById('bulk-edit-policyholder-btn');
    const bulkDeleteBtn = document.getElementById('bulk-delete-policyholder-btn');
    
    console.log('Elements found:', {
        selectAllCheckbox: !!selectAllCheckbox,
        policyholderCheckboxes: policyholderCheckboxes.length,
        bulkEditBtn: !!bulkEditBtn,
        bulkDeleteBtn: !!bulkDeleteBtn
    });
    
    if (!selectAllCheckbox || !bulkEditBtn || !bulkDeleteBtn) {
        console.error('Missing required elements for policyholder selection');
        return;
    }
    
    // Select all checkbox functionality
    selectAllCheckbox.addEventListener('change', function() {
        policyholderCheckboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        });
    });
    
    // Bulk edit button
    bulkEditBtn.addEventListener('click', function() {
        const selectedIds = Array.from(document.querySelectorAll('.policyholder-checkbox:checked'))
            .map(checkbox => checkbox.getAttribute('data-policyholder-id'));
        
        if (selectedIds.length === 0) {
            showErrorNotification('Please select at least one policyholder to edit.');
            return;
        }
        
        if (selectedIds.length === 1) {
            // If only one policyholder is selected, use the existing edit function
            editPolicyholder(selectedIds[0]);
        } else if (selectedIds.length > 1) {
            // For multiple selections, show error notification
            showErrorNotification('Bulk editing of multiple policyholders is not yet implemented.');
            // Future enhancement: implement bulk edit functionality
        }
    });
    
    // Bulk delete button
    bulkDeleteBtn.addEventListener('click', function() {
        const selectedIds = Array.from(document.querySelectorAll('.policyholder-checkbox:checked'))
            .map(checkbox => checkbox.getAttribute('data-policyholder-id'));
        
        if (selectedIds.length === 0) {
            showErrorNotification('Please select at least one policyholder to delete.');
            return;
        }
        
        showConfirmNotification(
            `Are you sure you want to delete ${selectedIds.length} selected policyholders?`, 
            () => {
                // Delete each selected policyholder
                Promise.all(selectedIds.map(id => 
                    apiRequest(`/api/policyholders/${id}`, 'DELETE')
                ))
                .then(() => {
                    // Reload the policyholders list after successful deletion
                    loadPolicyholders();
                    showNotification('success', `Successfully deleted ${selectedIds.length} policyholders.`);
                })
                .catch(err => {
                    console.error('Error deleting policyholders:', err);
                    showErrorNotification('Failed to delete some policyholders. Please try again.');
                });
            }
        );
    });
}

// Function to show error notification
function showErrorNotification(message) {
    const errorNotification = document.getElementById('error-notification');
    const errorMessage = document.getElementById('error-message');
    
    if (errorNotification && errorMessage) {
        errorMessage.textContent = message;
        errorNotification.classList.remove('d-none');
        
        // Auto-hide after 5 seconds
        setTimeout(() => {
            errorNotification.classList.add('d-none');
        }, 5000);
    } else {
        // Fallback to alert if elements not found
        alert(message);
    }
}

// Function to show success notification
function showSuccessNotification(message) {
    const successNotification = document.getElementById('success-notification');
    const successMessage = document.getElementById('success-message');
    
    if (successNotification && successMessage) {
        successMessage.textContent = message;
        successNotification.classList.remove('d-none');
        
        // Auto-hide after 5 seconds
        setTimeout(() => {
            successNotification.classList.add('d-none');
        }, 5000);
    } else {
        // Fallback to alert if elements not found
        alert(message);
    }
}

// Function to show confirmation notification
function showConfirmNotification(message, onConfirm) {
    const confirmNotification = document.getElementById('confirm-notification');
    const confirmMessage = document.getElementById('confirm-message');
    const confirmYesBtn = document.getElementById('confirm-yes-btn');
    const confirmNoBtn = document.getElementById('confirm-no-btn');
    const confirmCloseBtn = document.getElementById('confirm-close-btn');
    
    if (confirmNotification && confirmMessage && confirmYesBtn && confirmNoBtn) {
        // Set message and show notification
        confirmMessage.textContent = message;
        confirmNotification.classList.remove('d-none');
        
        // Set up event listeners for buttons
        const yesHandler = () => {
            confirmNotification.classList.add('d-none');
            confirmYesBtn.removeEventListener('click', yesHandler);
            confirmNoBtn.removeEventListener('click', noHandler);
            if (confirmCloseBtn) {
                confirmCloseBtn.removeEventListener('click', noHandler);
            }
            if (typeof onConfirm === 'function') {
                onConfirm();
            }
        };
        
        const noHandler = () => {
            confirmNotification.classList.add('d-none');
            confirmYesBtn.removeEventListener('click', yesHandler);
            confirmNoBtn.removeEventListener('click', noHandler);
            if (confirmCloseBtn) {
                confirmCloseBtn.removeEventListener('click', noHandler);
            }
        };
        
        // Clean up any existing event listeners
        confirmYesBtn.replaceWith(confirmYesBtn.cloneNode(true));
        confirmNoBtn.replaceWith(confirmNoBtn.cloneNode(true));
        if (confirmCloseBtn) {
            confirmCloseBtn.replaceWith(confirmCloseBtn.cloneNode(true));
        }
        
        // Get fresh references after cloning
        const newYesBtn = document.getElementById('confirm-yes-btn');
        const newNoBtn = document.getElementById('confirm-no-btn');
        const newCloseBtn = document.getElementById('confirm-close-btn');
        
        // Add event listeners to new buttons
        newYesBtn.addEventListener('click', yesHandler);
        newNoBtn.addEventListener('click', noHandler);
        if (newCloseBtn) {
            newCloseBtn.addEventListener('click', noHandler);
        }
    } else {
        // Fallback to confirm if elements not found
        if (confirm(message) && typeof onConfirm === 'function') {
            onConfirm();
        }
    }
}

async function loadDashboard(forceRefresh = false) {
    try {
        // Use the centralized stats function instead of direct API call
        const stats = await getDashboardStats(forceRefresh);
        
        // summary cards
        const totalPolicyholdersEl = document.getElementById('totalPolicyholders');
        const activePoliciesEl = document.getElementById('activePolicies');
        const pendingRenewalsEl = document.getElementById('pendingRenewals');
        const expiredPoliciesEl = document.getElementById('expiredPolicies');
        
        if (totalPolicyholdersEl) totalPolicyholdersEl.textContent = stats.totalPolicyholders;
        if (activePoliciesEl) activePoliciesEl.textContent = stats.activePolicies;
        if (pendingRenewalsEl) pendingRenewalsEl.textContent = stats.pendingRenewals;
        if (expiredPoliciesEl) expiredPoliciesEl.textContent = stats.expiredPolicies;
        
        // if you have a table for individual items
        if (stats.items && document.getElementById('policyTableBody')) {
            const body = document.getElementById('policyTableBody');
            body.innerHTML = stats.items.map(i => `
                <tr>
                    <td>${i.policy_id}</td>
                    <td>${i.policy_type}</td>
                    <td>${i.policyholder_id}</td>
                    <td>${formatDate(i.startDate)}</td>
                    <td>${formatDate(i.endDate)}</td>
                    <td><span class="badge bg-${badgeClass(i.status_name)}">${i.status_name}</span></td>
                </tr>
            `).join('');
        }
        
        // Dispatch an event to notify other components that stats are updated
        window.dispatchEvent(new CustomEvent('stats-updated', { detail: stats }));
    } catch (err) {
        console.error('Error loading dashboard:', err);
        showError('Failed to load dashboard.');
    }
}

async function loadReports() {
    try {
        const stats = await apiRequest('/api/reports/policy-performance');
        
        // summary cards
        document.getElementById('totalPolicies').textContent = stats.totalPolicies;
        document.getElementById('activePolicies').textContent = stats.activePolicies;
        document.getElementById('pendingRenewals').textContent = stats.pendingRenewals;
        document.getElementById('expiredPolicies').textContent = stats.expiredPolicies;
        
        // if you have a table for individual items
        if (stats.items && document.getElementById('policyTableBody')) {
            const body = document.getElementById('policyTableBody');
            body.innerHTML = stats.items.map(i => `
                <tr>
                    <td>${i.policy_id}</td>
                    <td>${i.policy_type}</td>
                    <td>${i.policyholder_id}</td>
                    <td>${formatDate(i.startDate)}</td>
                    <td>${formatDate(i.endDate)}</td>
                    <td><span class="badge bg-${badgeClass(i.status_name)}">${i.status_name}</span></td>
                </tr>
            `).join('');
        }
    } catch (err) {
        console.error('Error loading reports:', err);
        showError('Failed to load reports.');
    }
}

function badgeClass(status) {
    return {
        'Active': 'success',
        'Cancelled': 'warning',
        'Expired': 'danger',
        'Pending': 'info',
        'Unknown': 'secondary'
    }[status] ?? 'secondary';
}

function formatDate(iso) {
    if (!iso) return 'N/A';
    return new Date(iso).toLocaleDateString();
}

function renderTable(dataList, columns, type) {
    if (!dataList || dataList.length === 0) return '<p>No data available.</p>';
    
    const showActions = !(type === 'analytics' || type === 'policyholder' || type === 'payment' || type === 'policy');
    const tableClass = type === 'payment' ? 'table table-hover table-financial' : 'table table-hover';
    
    let thead = '<tr>' + columns.map(col => `<th>${col.header}</th>`).join('') + (showActions ? '<th>Actions</th>' : '') + '</tr>';
    let tbody = dataList.map(row => {
        const idKey = columns[0]?.key;
        const rowId = idKey ? row[idKey] : null;
        const cells = columns.map(col => {
            const cellValue = row[col.key] ?? '';
            
            // Special formatting for Status column in payment table
            if (col.key === 'latestStatus' && type === 'payment') {
                const statusClass = getStatusBadgeClass(cellValue);
                return `<td><span class="badge ${statusClass}">${cellValue}</span></td>`;
            } else if (col.header === 'Status') {
                return `<td><span class="badge bg-${badgeClass(cellValue)}">${cellValue}</span></td>`;
            } else {
                return `<td>${cellValue}</td>`;
            }
        }).join('');
        return '<tr>' + cells + '</tr>';
    }).join('');
    
    return `<div class="table-responsive"><table class="${tableClass}"><thead>${thead}</thead><tbody>${tbody}</tbody></table></div>`;
}

function getStatusBadgeClass(status) {
    if (!status || status === 'N/A') return 'badge-status status-na';
    const statusLower = status.toLowerCase();
    if (statusLower.includes('paid') || statusLower.includes('completed')) return 'badge-status status-paid';
    if (statusLower.includes('pending')) return 'badge-status status-pending';
    if (statusLower.includes('failed') || statusLower.includes('overdue')) return 'badge-status status-failed';
    return 'badge-status status-na';
}

async function loadEditPolicyStatuses(selectedStatusId) {
    const select = document.getElementById('edit-policy-status');
    select.innerHTML = '<option value="">Select Status</option>';
    try {
        const statuses = await apiRequest('/api/policies/policy-statuses');
        statuses.forEach(status => {
            const option = document.createElement('option');
            option.value = status.statusId;
            option.textContent = status.statusName;
            if (status.statusId === selectedStatusId) option.selected = true;
            select.appendChild(option);
        });
    } catch (err) {
        select.innerHTML = '<option value="">Failed to load statuses</option>';
    }
}

async function editRow(type, id) {
    if (type === 'policy') {
        const data = await apiRequest(`/api/insurance-coverage/${id}`);
        console.log('Policy data:', data); 

        // Fill the edit fields
        document.getElementById('edit-policy-endDate').value = data.expirationDate || '';
        document.getElementById('edit-policy-premium').value = data.premium || '';
        document.getElementById('edit-policy-coverage').value = data.coverageAmount || '';
        // Load and set status
        await loadEditPolicyStatuses(data.status?.statusId || data.statusId);
        document.getElementById('edit-policy-status').value = data.status?.statusId || data.statusId || '';

        // Show the edit section
        const modalEl = document.getElementById('editPolicyModal');
        let modal;
        if (modalEl) {
            modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
            modal.show();
        }

        //Edite form submission
        const editPolicyForm = document.getElementById('editPolicyForm');
        editPolicyForm.onsubmit = async (event) => {
            event.preventDefault();
            
            // Validate the form using our validation utility
            if (!window.ValidationUtils.validateForm(editPolicyForm)) {
                // Form has validation errors, don't proceed with submission
                return;
            }
            
            const updatedData = {
                expiration_date: document.getElementById('edit-policy-endDate').value,
                premium: document.getElementById('edit-policy-premium').value,
                coverage_amount: document.getElementById('edit-policy-coverage').value,
                status_id: document.getElementById('edit-policy-status').value
            };
            try {
                await apiRequest(`/api/insurance-coverage/${id}`, 'PUT', updatedData);
                showSuccess('Policy updated successfully!');
                modal.hide();
                setTimeout(() => location.reload(), 1000);
            } catch (err) {
                console.error('Failed to update policy: ' + err.message);
                showError('Failed to update policy: ' + err.message);
            }
        };
    }
}

async function deleteRow(type, id) {
    if (type === 'policy') {
        showConfirmNotification(
            `Are you sure you want to delete this policy?`, 
            () => {
                // Delete each selected policy
                apiRequest(`/api/insurance-coverage/${id}`, 'DELETE')
                .then(() => {
                    // Reload the policies list after successful deletion
                    loadPolicies();
                    showSuccess('Policy deleted successfully!');
                })
                .catch(err => {
                    console.error('Failed to delete policy: ' + err.message);
                    showError('Failed to delete policy: ' + err.message);
                });
            }
        );
    }
}

function editPolicy(id) {
    editRow('policy', id);
}

function deletePolicy(id) {
    showConfirmNotification(
        `Are you sure you want to delete this policy?`, 
        () => {
            // Delete each selected policy
            apiRequest(`/api/insurance-coverage/${id}`, 'DELETE')
            .then(() => {
                // Reload the policies list after successful deletion
                loadPolicies();
                showSuccess('Policy deleted successfully!');
            })
            .catch(err => {
                console.error('Failed to delete policy: ' + err.message);
                showError('Failed to delete policy: ' + err.message);
            });
        }
    );
}

// --- API Request Helper ---
async function apiRequest(url, method = 'GET', body = null) {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error('No authentication token found - redirecting to login');
        window.location.href = '/index.html';
        throw new Error('No authentication token');
    }
    
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
    };
    if (body && method !== 'GET') {
        options.body = JSON.stringify(body);
    }
    const response = await fetch(url, options);
    if (!response.ok) {
        let errorMessage;
        try {
            // Try to parse as JSON first
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                console.error(`API Error ${response.status}:`, errorData);
                errorMessage = JSON.stringify(errorData);
            } else {
                const errorText = await response.text();
                console.error(`API Error ${response.status}: ${errorText}`);
                errorMessage = errorText;
            }
        } catch (parseError) {
            // If any parsing fails, use a generic error message
            console.error(`API Error ${response.status}: Failed to parse error response`, parseError);
            errorMessage = 'Unknown error occurred';
        }
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorMessage}`);
    }
    // Handle cases where the response might be empty (e.g., DELETE 204 No Content)
    const contentType = response.headers.get('content-type');
    if (response.status === 204 || !contentType || !contentType.includes('application/json')) {
        return null; // Or return a specific indicator like { success: true }
    }
    return response.json();
}

// Get Logged In User ID 
function getLoggedInAgentId() {
    const token = localStorage.getItem('token');
    if (!token) {
        console.error('No authentication token found');
        return null;
    }
    try {
        const payload = token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payload));
        console.log('JWT payload:', decodedPayload);
        const agentId = decodedPayload.sub;
        if (!agentId || isNaN(agentId)) {
            console.warn('No valid agent ID found in token. Using default agent ID');
            return 1;
        }
        return parseInt(agentId, 10);
    } catch (error) {
        console.error('Error extracting agent ID from token:', error);
        return 1;
    }
}

// --- Load Policy Types for Add Modal ---
async function loadPolicyTypes() {
    try {
        console.log('Fetching policy types...');
        const types = await apiRequest('/api/policies/policy-types'); 
        const select = document.getElementById('policy-type');
        select.innerHTML = '<option value="">Select Policy Type</option>';
        types.forEach(type => {
            const option = document.createElement('option');
            option.value = type.policyId; 
            option.textContent = type.policyType; 
            select.appendChild(option);
        });
        console.log('Policy types loaded.');
    } catch (error) {
        console.error('Failed to load policy types:', error);

    }
}

// Add Policy 
const addPolicyModal = document.getElementById('addPolicyModal');
if (addPolicyModal) {
    addPolicyModal.addEventListener('show.bs.modal', () => {
        console.log('Add Policy Modal opened. Loading data...');
        // Reset form 
        document.getElementById('addPolicyForm').reset(); 
        // Load data
        loadPolicyTypes();
    });
}

//Add Policy Form Submission
const addPolicyForm = document.getElementById('addPolicyForm');
if (addPolicyForm) {
    addPolicyForm.addEventListener('submit', async (event) => {
        event.preventDefault(); 
        console.log('Submitting Add Policy form...');

        // Validate the form using our validation utility
        if (!window.ValidationUtils.validateForm(addPolicyForm)) {
            // Form has validation errors, don't proceed with submission
            return;
        }

        // **Temporary:** Get policyholder ID directly from the search input.
        // **TODO:** Implement proper search and selection to populate a hidden #policy-holder-id field.
        const policyholderIdSearch = document.getElementById('policy-holder-search').value.trim();
        const policyholderId = parseInt(policyholderIdSearch); 
        
        if (!policyholderId || isNaN(policyholderId)) {
            showError('Please enter a valid Policyholder ID.');
            return;
        }

        const policyId = document.getElementById('policy-type').value;
        const expirationDate = document.getElementById('policy-endDate').value;
        const premium = document.getElementById('policy-premium').value;
        const coverageAmount = document.getElementById('policy-coverage').value;

        // Get the logged-in agent ID
        const agentId = getLoggedInAgentId();
        if (!agentId) {
            alert('Error: Could not determine the agent ID. Please log in again.');
            return;
        }

        console.log(`Creating policy for policyholder ${policyholderId} with agent ${agentId}`);

        const policyData = {
            policy_id: policyId,
            policyholder_id: policyholderId,
            expiration_date: expirationDate,
            premium: premium,
            coverage_amount: coverageAmount,
            status_id: 3,//defulte status for now policy
            agent_id: agentId
        };

        console.log('Sending policy data:', policyData);

        try {
            const result = await apiRequest('/api/insurance-coverage', 'POST', policyData);
            console.log('Policy created successfully:', result);
            
            const modalEl = document.getElementById('addPolicyModal');
            if (modalEl) {
                const modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
                modal.hide();
            }
            showSuccess('Policy added successfully!');
            setTimeout(() => location.reload(), 1500);
        } catch (error) {
            console.error('Failed to add policy:', error);
            showError(`Failed to add policy. Error: ${error.message}`);
        }
    });
}



// Policyholder Functions
async function addPolicyholder(event) {
    event.preventDefault();
    const form = document.getElementById('addPolicyholderForm');
    
    // Validate the form using our validation utility
    if (!window.ValidationUtils.validateForm(form)) {
        // Form has validation errors, don't proceed with submission
        return;
    }
    
    const formData = new FormData(form);
    
    // data to object
    const payload = {
        ssn: formData.get('ssn'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        phoneNumber: formData.get('phoneNumber'),
        email: formData.get('email'),
        password: formData.get('password'),
        gender: formData.get('gender'),
        birthdate: formData.get('birthdate'),
        enrollmentDate: formData.get('enrollmentDate'),
        state: formData.get('state'),
        city: formData.get('city'),
        street: formData.get('street'),
        zipCode: formData.get('zipCode')
    };

    // Handle optional dependant fields
    const dependantId = formData.get('dependantId');
    const dependantRelation = formData.get('dependantRelation');
    if (dependantId && dependantRelation) {
        payload.dependant = { policyholderId: parseInt(dependantId) };
        payload.dependantRelation = dependantRelation;
    }

    try {
        const response = await apiRequest('/api/policyholders', 'POST', payload);
        bootstrap.Modal.getInstance(document.getElementById('addPolicyholderModal')).hide();
        loadPolicyholders();
        form.reset();
        // Use showSuccessNotification for success message
        showSuccessNotification('Policyholder added successfully!');
    } catch (err) {
        console.error('Error adding policyholder:', err);
        
        // Always close the modal when there's an error
        const modal = bootstrap.Modal.getInstance(document.getElementById('addPolicyholderModal'));
        if (modal) {
            modal.hide();
        }
        
        // Check if it's an access denied error (403)
        if (err.status === 403 || (err.message && err.message.includes('Access denied'))) {
            // Show access denied notification
            showErrorNotification('Access denied');
        } else {
            // Show generic error notification
            showErrorNotification(err.message || 'Failed to add policyholder');
        }
    }
}

async function editPolicyholder(id) {
    try {
        
        const data = await apiRequest(`/api/policyholders/${id}`);
        
        const form = document.getElementById('editPolicyholderForm');
        form.querySelector('#edit-ph-ssn').value = data.ssn;
        form.querySelector('#edit-ph-firstName').value = data.firstName;
        form.querySelector('#edit-ph-lastName').value = data.lastName;
        form.querySelector('#edit-ph-phoneNumber').value = data.phoneNumber;
        form.querySelector('#edit-ph-email').value = data.email;
        form.querySelector('#edit-ph-password').value = data.password;
        form.querySelector('#edit-ph-gender').value = data.gender;
        form.querySelector('#edit-ph-birthdate').value = data.birthdate;
        form.querySelector('#edit-ph-enrollmentDate').value = data.enrollmentDate;
        form.querySelector('#edit-ph-state').value = data.state;
        form.querySelector('#edit-ph-city').value = data.city;
        form.querySelector('#edit-ph-street').value = data.street;
        form.querySelector('#edit-ph-zipCode').value = data.zipCode || '';
        
        
        if (data.dependant) {
            form.querySelector('#edit-ph-dependantId').value = data.dependant.policyholderId;
            form.querySelector('#edit-ph-dependantRelation').value = data.dependantRelation || '';
        } else {
            form.querySelector('#edit-ph-dependantId').value = '';
            form.querySelector('#edit-ph-dependantRelation').value = '';
        }

        // Set the policyholder ID on the form for update
        form.dataset.policyholderId = id;

        const modal = new bootstrap.Modal(document.getElementById('editPolicyholderModal'));
        modal.show();
    } catch (err) {
        console.error('Error loading policyholder:', err);
        showError('Failed to load policyholder data');
    }
}

async function updatePolicyholder(event, id) {
    event.preventDefault();
    const form = document.getElementById('editPolicyholderForm');
    
    // Validate the form using our validation utility
    if (!window.ValidationUtils.validateForm(form)) {
        // Form has validation errors, don't proceed with submission
        return;
    }
    
    const formData = new FormData(form);
    
    // Convert form data to object
    const payload = {
        ssn: formData.get('ssn'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        phoneNumber: formData.get('phoneNumber'),
        email: formData.get('email'),
        gender: formData.get('gender'),
        birthdate: formData.get('birthdate'),
        enrollmentDate: formData.get('enrollmentDate'),
        state: formData.get('state'),
        city: formData.get('city'),
        street: formData.get('street'),
        zipCode: formData.get('zipCode')
    };

    // Handle optional dependant fields
    const dependantId = formData.get('dependantId');
    const dependantRelation = formData.get('dependantRelation');
    if (dependantId && dependantRelation) {
        payload.dependant = { policyholderId: parseInt(dependantId) };
        payload.dependantRelation = dependantRelation;
    }

    try {
        const response = await apiRequest(`/api/policyholders/${id}`, 'PUT', payload);
        // Close modal and refresh list
        bootstrap.Modal.getInstance(document.getElementById('editPolicyholderModal')).hide();
        loadPolicyholders();
        showSuccess('Policyholder updated successfully!');
    } catch (err) {
        console.error('Error updating policyholder:', err);
        showError(err.message || 'Failed to update policyholder');
    }
}

async function deletePolicyholder(id) {
    showConfirmNotification(
        `Are you sure you want to delete this policyholder?`, 
        () => {
            // Delete each selected policyholder
            apiRequest(`/api/policyholders/${id}`, 'DELETE')
            .then(() => {
                // Reload the policyholders list after successful deletion
                loadPolicyholders();
                showSuccess('Policyholder deleted successfully!');
            })
            .catch(err => {
                console.error('Error deleting policyholder:', err);
                showError('Failed to delete policyholder');
            });
        }
    );
}

// policyholder forms addEventListener
document.addEventListener('DOMContentLoaded', () => {
    const addPolicyholderForm = document.getElementById('addPolicyholderForm');
    if (addPolicyholderForm) {
        addPolicyholderForm.addEventListener('submit', addPolicyholder);
    }

    const editPolicyholderForm = document.getElementById('editPolicyholderForm');
    if (editPolicyholderForm) {
        editPolicyholderForm.addEventListener('submit', (event) => {
            const id = event.target.dataset.policyholderId;
            updatePolicyholder(event, id);
        });
    }
});