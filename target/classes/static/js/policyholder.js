// Utility functions
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

function formatDate(iso) {
    if (!iso) return 'N/A';
    const date = new Date(iso);
    return date.toLocaleDateString();
}

function authHeaders() {
    const token = localStorage.getItem('policyholderToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// Helper function to get the appropriate status class based on status name
function getStatusClass(status) {
    switch (status) {
        case 'Approved':
            return 'bg-success';
        case 'Filed':
        case 'Under Review':
            return 'bg-warning';
        case 'Rejected':
            return 'bg-danger';
        case 'Closed':
            return 'bg-info';
        default:
            return 'bg-secondary';
    }
}

async function apiRequest(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: authHeaders()
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(url, options);
    
    if (response.status === 401) {
        // Unauthorized - redirect to login
        localStorage.removeItem('policyholderToken');
        localStorage.removeItem('policyholderData');
        window.location.href = 'policyholder-login.html';
        throw new Error('Session expired. Please login again.');
    }
    
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const error = new Error(errorData.message || `HTTP error! status: ${response.status}`);
        error.status = response.status;
        throw error;
    }
    
    // Check if the response is empty
    const text = await response.text();
    return text ? JSON.parse(text) : {};
}

// Login page functionality
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('policyholder-login-form');
    const registerForm = document.getElementById('policyholder-register-form');
    const showRegisterLink = document.getElementById('showPolicyholderRegister');
    const showLoginButton = document.getElementById('showLogin');
    const loginCard = document.getElementById('login-card');
    const registerCard = document.getElementById('register-card');
    
    // Toggle between login and registration forms
    if (showRegisterLink) {
        showRegisterLink.addEventListener('click', (event) => {
            event.preventDefault();
            loginCard.style.display = 'none';
            registerCard.style.display = 'block';
        });
    }
    
    if (showLoginButton) {
        showLoginButton.addEventListener('click', () => {
            registerCard.style.display = 'none';
            loginCard.style.display = 'block';
        });
    }
    
    // Handle login form submission
    if (loginForm) {
        // We're on the login page
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            try {
                const response = await fetch('/api/auth/policyholder/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password })
                });
                
                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || 'Login failed. Please check your credentials.');
                }
                
                const data = await response.json();
                
                // Store token and redirect to dashboard
                localStorage.setItem('policyholderToken', data.token);
                localStorage.setItem('policyholderData', JSON.stringify(data.policyholder));
                
                showSuccessNotification('Login successful!');
                
                // Redirect after a short delay to allow the notification to be seen
                setTimeout(() => {
                    window.location.href = 'policyholder-dashboard.html';
                }, 1000);
                
            } catch (error) {
                console.error('Login error:', error);
                showErrorNotification(error.message || 'Login failed. Please try again.');
            }
        });
    }
    
    // Handle registration form submission
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            
            // Collect form data
            const formData = {
                firstName: document.getElementById('register-firstName').value,
                lastName: document.getElementById('register-lastName').value,
                email: document.getElementById('register-email').value,
                password: document.getElementById('register-password').value,
                phoneNumber: document.getElementById('register-phone').value,
                gender: document.getElementById('register-gender').value,
                birthdate: document.getElementById('register-birthdate').value,
                street: document.getElementById('register-street').value,
                city: document.getElementById('register-city').value,
                state: document.getElementById('register-state').value,
                zipCode: document.getElementById('register-zipCode').value
            };
            
            try {
                const response = await fetch('/api/policyholder-auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });
                
                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || 'Registration failed. Please try again.');
                }
                
                showSuccessNotification('Registration successful! You can now log in.');
                
                // Clear form and show login form after successful registration
                registerForm.reset();
                registerCard.style.display = 'none';
                loginCard.style.display = 'block';
                
            } catch (error) {
                console.error('Registration error:', error);
                showErrorNotification(error.message || 'Registration failed. Please try again.');
            }
        });
    }
    
    // Check if we're on the dashboard page
    if (!loginForm && !registerForm) {
        // We're on the dashboard page, but initialization is handled by the dedicated dashboard initializer below
    }
});

// Policyholder Portal functionality (Dashboard, Payments, Claims)
document.addEventListener('DOMContentLoaded', function() {
    const isDashboardPage = window.location.href.includes('policyholder-dashboard.html');
    const isPaymentsPage = window.location.href.includes('policyholder-payments.html');
    const isClaimsPage = window.location.href.includes('policyholder-claims.html');
    
    // Check if user is on any policyholder portal page
    if (isDashboardPage || isPaymentsPage || isClaimsPage) {
        // Check if user is authenticated
        const token = localStorage.getItem('policyholderToken');
        if (!token) {
            // Redirect to login if no token
            window.location.href = 'policyholder-login.html';
            return;
        }
        
        // Mark body as authenticated to show content
        document.body.classList.add('authenticated');
        
        // Setup logout functionality
        const logoutLink = document.getElementById('logout-link');
        if (logoutLink) {
            logoutLink.addEventListener('click', function(e) {
                e.preventDefault();
                localStorage.removeItem('policyholderToken');
                localStorage.removeItem('policyholderData');
                window.location.href = 'policyholder-login.html';
            });
        }
        
        // Load page-specific content
        if (isDashboardPage) {
            loadPolicyholderData();
            loadPolicies();
        } else if (isPaymentsPage) {
            loadPolicyholderData();
            loadPayments();
            setupPaymentForm();
        } else if (isClaimsPage) {
            loadPolicyholderData();
            loadClaims();
            setupClaimForm();
        }
    }
});

async function loadPolicyholderData() {
    try {
        // First try to get data from localStorage
        const storedData = localStorage.getItem('policyholderData');
        let policyholderData;
        
        if (storedData) {
            policyholderData = JSON.parse(storedData);
        } else {
            // If not available, fetch from API
            policyholderData = await apiRequest('/api/policyholder/profile', 'GET');
            localStorage.setItem('policyholderData', JSON.stringify(policyholderData));
        }
        
        // Update the UI with policyholder data - with safe element updates
        function updateElementText(id, text) {
            const element = document.getElementById(id);
            if (element) {
                element.textContent = text;
            }
        }
        
        // Update name fields
        const fullName = `${policyholderData.firstName || ''} ${policyholderData.lastName || ''}`.trim() || 'Policyholder';
        updateElementText('policyholder-name', fullName);
        updateElementText('full-name', fullName);
        
        // Update other personal information
        updateElementText('email-display', policyholderData.email || 'Not provided');
        updateElementText('phone-display', policyholderData.phoneNumber || 'Not provided');
        updateElementText('gender-display', policyholderData.gender || 'Not specified');
        
        // Handle address with fallbacks for missing fields
        const street = policyholderData.street || '';
        const city = policyholderData.city || '';
        const state = policyholderData.state || '';
        const zipCode = policyholderData.zipCode || '';
        const formattedAddress = [street, city, state, zipCode].filter(Boolean).join(', ') || 'Not provided';
        updateElementText('address-display', formattedAddress);
        
        // Update birthdate
        updateElementText('birthdate-display', formatDate(policyholderData.birthdate));
        
        // Data loaded successfully
        
    } catch (error) {
        console.error('Error loading policyholder data:', error);
        showErrorNotification('Failed to load your profile information.');
    }
}

async function loadPolicies() {
    try {
        const response = await fetch('/api/policyholder/policies', {
            method: 'GET',
            headers: authHeaders()
        });
        
        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }
        
        const policies = await response.json();
        const policiesTable = document.getElementById('policies-list');
        
        if (!policiesTable) {
            return; // Not on the dashboard page
        }
        
        if (policies.length === 0) {
            policiesTable.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center">No policies found.</td>
                </tr>
            `;
            return;
        }
        
        policiesTable.innerHTML = policies.map(policy => {
            // Get status class for badge
            let statusClass = 'bg-secondary'; // Default
            let statusName = 'Unknown';
            
            // Check if policy has status property
            if (policy.status) {
                if (typeof policy.status === 'object' && policy.status.statusName) {
                    statusName = policy.status.statusName;
                } else if (typeof policy.status === 'string') {
                    statusName = policy.status;
                }
                
                // Set appropriate class based on status name
                switch (statusName.toLowerCase()) {
                    case 'active':
                        statusClass = 'bg-success';
                        break;
                    case 'expired':
                        statusClass = 'bg-danger';
                        break;
                    case 'pending':
                        statusClass = 'bg-warning';
                        break;
                    case 'cancelled':
                        statusClass = 'bg-danger';
                        break;
                    case 'renewed':
                        statusClass = 'bg-info';
                        break;
                    default:
                        statusClass = 'bg-secondary';
                }
            }
            
            // Get policy type
            let policyType = 'Standard';
            if (policy.policy && policy.policy.policyType) {
                policyType = policy.policy.policyType;
            } else if (policy.policyType) {
                policyType = policy.policyType;
            }
            
            // Format coverage amount and premium with fallbacks
            const coverageAmount = policy.coverageAmount ? policy.coverageAmount : 0;
            const premium = policy.premium ? policy.premium : 0;
            
            return `
                <tr>
                    <td>${policy.insuranceId}</td>
                    <td>${policyType}</td>
                    <td>$${coverageAmount.toLocaleString()}</td>
                    <td>$${premium.toLocaleString()}</td>
                    <td>${formatDate(policy.expirationDate)}</td>
                    <td><span class="badge ${statusClass}">${statusName}</span></td>
                </tr>
            `;
        }).join('');
        
        // Store policies in localStorage for other pages to use
        localStorage.setItem('policyholderPolicies', JSON.stringify(policies));
        
    } catch (error) {
        console.error('Error loading policies:', error);
        showErrorNotification('Failed to load your policies.');
    }
}

// Helper function to get the latest status of a payment
const getPaymentStatus = (payment) => {
    // First check if latestStatus is available from the API response
    if (payment.latestStatus) {
        return payment.latestStatus;
    }
    
    // Then check payment status history if available
    if (payment.paymentStatuses && payment.paymentStatuses.length > 0) {
        // Sort statuses by date (newest first) and get the first one
        const sortedStatuses = [...payment.paymentStatuses].sort((a, b) => {
            return new Date(b.statusDate) - new Date(a.statusDate);
        });
        
        return sortedStatuses[0].statusDetails?.statusName || sortedStatuses[0].statusName || 'Unknown';
    }
    
    // Fallback to inferring status from payment date
    if (payment.paymentDate) {
        // If payment has been made, assume it's paid unless we know otherwise
        return 'Paid';
    }
    
    // Check if payment is overdue
    if (payment.dueDate) {
        const dueDate = new Date(payment.dueDate);
        const today = new Date();
        if (dueDate < today) {
            return 'Overdue';
        }
        return 'Pending';
    }
    
    return 'Unknown'; // Default if status cannot be determined
};

// Payments functionality
async function loadPayments() {
    try {
        // Hide any existing error notifications
        const errorNotification = document.getElementById('error-notification');
        if (errorNotification) {
            errorNotification.classList.add('d-none');
        }
        
        // Fetch payments using the apiRequest function
        const payments = await apiRequest('/api/policyholder/payments', 'GET');
        
        // Update payment statistics
        const totalPayments = document.getElementById('total-payments');
        const paidPayments = document.getElementById('paid-payments');
        const pendingPayments = document.getElementById('pending-payments');
        const overduePayments = document.getElementById('overdue-payments');
        
        if (totalPayments) totalPayments.textContent = payments.length;
        if (paidPayments) paidPayments.textContent = payments.filter(p => getPaymentStatus(p) === 'Paid').length;
        if (pendingPayments) pendingPayments.textContent = payments.filter(p => getPaymentStatus(p) === 'Pending').length;
        if (overduePayments) overduePayments.textContent = payments.filter(p => getPaymentStatus(p) === 'Overdue').length;
        
        // Store payments in localStorage for reference
        localStorage.setItem('policyholderPayments', JSON.stringify(payments));
        
        // Populate payments table
        const paymentsTable = document.getElementById('payments-list');
        if (!paymentsTable) return;
        
        if (payments.length === 0) {
            paymentsTable.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center">No payments found.</td>
                </tr>
            `;
            return;
        }
        
        paymentsTable.innerHTML = payments.map(payment => {
            // Get status class for badge
            let statusClass = '';
            const statusText = getPaymentStatus(payment); // Use helper to get status

            switch (statusText) { // Switch on derived status
                case 'Paid':
                    statusClass = 'bg-success';
                    break;
                case 'Pending':
                    statusClass = 'bg-warning';
                    break;
                case 'Overdue':
                    statusClass = 'bg-danger';
                    break;
                case 'Upcoming':
                    statusClass = 'bg-info';
                    break;
                default: // Handles 'Unknown' or other statuses
                    statusClass = 'bg-secondary';
            }
            
            // Determine action button based on status
            let actionButton = '';
            if (statusText === 'Pending' || statusText === 'Overdue') {
                actionButton = `<button class="btn btn-primary btn-sm payment-action" onclick="showPaymentModal(${payment.paymentId})">Pay Now</button>`;
            } else if (statusText === 'Paid') {
                actionButton = `<button class="btn btn-outline-secondary btn-sm payment-action" onclick="showReceiptModal(${payment.paymentId})">Receipt</button>`;
            } else {
                actionButton = `<span class="text-muted">No action</span>`;
            }
            
            // Get policy ID from the payment object
            let policyIdForDisplay = 'N/A';
            
            // First try the insuranceId field which is the actual policy ID
            if (payment.insuranceId) {
                policyIdForDisplay = payment.insuranceId;
            }
            // Then try to get it from the insurance object if available
            else if (payment.insurance && payment.insurance.insuranceId) {
                policyIdForDisplay = payment.insurance.insuranceId;
            }
            // For payments that don't have any ID information, try to get it from stored policies
            else {
                // Try to find matching policy from localStorage
                const storedPolicies = localStorage.getItem('policyholderPolicies');
                if (storedPolicies) {
                    const policies = JSON.parse(storedPolicies);
                    // Find a policy that might be related to this payment (this is a best-effort approach)
                    const matchingPolicy = policies.find(p => p.premium === payment.amount);
                    if (matchingPolicy) {
                        policyIdForDisplay = matchingPolicy.insuranceId;
                    }
                }
            }

            return `
                <tr>
                    <td>${payment.paymentId}</td>
                    <td>${policyIdForDisplay}</td>
                    <td>$${payment.amount.toLocaleString()}</td>
                    <td>${payment.paymentDate ? formatDate(payment.paymentDate) : 'Not paid'}</td>
                    <td>${formatDate(payment.dueDate)}</td>
                    <td><span class="badge ${statusClass} payment-status-badge">${statusText}</span></td>
                    <td>${actionButton}</td>
                </tr>
            `;
        }).join('');
        
    } catch (error) {
        console.error('Error loading payments:', error);
        showErrorNotification('Failed to load payments. Please try again later.');
    }
}

function setupPaymentForm() {
    // Populate policy dropdown in payment form
    const policySelect = document.getElementById('policy-select');
    if (!policySelect) return;
    
    // Get policies from localStorage
    const storedPolicies = localStorage.getItem('policyholderPolicies');
    if (storedPolicies) {
        const policies = JSON.parse(storedPolicies);
        
        // Add options to the select
        policies.forEach(policy => {
            const option = document.createElement('option');
            option.value = policy.insuranceId;
            option.textContent = `Policy #${policy.insuranceId} - ${policy.policyType || 'Standard'} ($${policy.premium ? policy.premium.toLocaleString() : '0'})`;
            policySelect.appendChild(option);
        });
    }
    
    // Set up payment form submission
    const submitPaymentBtn = document.getElementById('submit-payment-btn');
    if (submitPaymentBtn) {
        submitPaymentBtn.addEventListener('click', async function() {
            const paymentForm = document.getElementById('payment-form');
            if (!paymentForm.checkValidity()) {
                paymentForm.reportValidity();
                return;
            }
            
            try {
                const policyId = document.getElementById('policy-select').value;
                const amount = document.getElementById('payment-amount').value;
                const paymentMethod = document.getElementById('payment-method').value;
                const cardNumber = document.getElementById('card-number') ? document.getElementById('card-number').value : '';
                const cardExpiry = document.getElementById('card-expiry') ? document.getElementById('card-expiry').value : '';
                const cardCvv = document.getElementById('card-cvv') ? document.getElementById('card-cvv').value : '';
                
                const paymentData = {
                    policyId,
                    amount,
                    paymentMethod,
                    cardNumber,
                    cardExpiry,
                    cardCvv
                };
                
                let paymentSuccessful = false;
                let responseData = null;
                
                // Use apiRequest with fallback data
                try {
                    responseData = await apiRequest('/api/policyholder/payments', 'POST', paymentData);
                    
                    // If we get here, the request was successful
                    paymentSuccessful = true;
                    
                } catch (error) {
                    // Even with fallback, something went wrong
                    console.error('Payment submission failed:', error);
                    paymentSuccessful = false;
                }
                
                if (paymentSuccessful) {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('makePaymentModal'));
                    modal.hide();
                    
                    showSuccessNotification('Payment processed successfully!');
                    
                    // Reload payments to show the updated list
                    setTimeout(() => {
                        loadPayments();
                    }, 1000);
                } else {
                    throw new Error('Payment failed. Please try again.');
                }
            } catch (error) {
                console.error('Payment error:', error);
                showErrorNotification(error.message || 'Payment failed. Please try again.');
            }
        });
    }
    
    // Toggle card details based on payment method
    const paymentMethod = document.getElementById('payment-method');
    const cardDetails = document.getElementById('card-details');
    
    // Function to toggle required attribute on card fields
    const toggleCardFieldsRequired = (required) => {
        const cardFields = ['card-number', 'card-expiry', 'card-cvv'];
        cardFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field) {
                if (required) {
                    field.setAttribute('required', 'required');
                } else {
                    field.removeAttribute('required');
                }
            }
        });
    };
    
    if (paymentMethod && cardDetails) {
        // Initial setup based on default selection
        if (paymentMethod.value === 'bank_transfer') {
            cardDetails.style.display = 'none';
            toggleCardFieldsRequired(false);
        }
        
        // Handle changes to payment method
        paymentMethod.addEventListener('change', function() {
            if (this.value === 'bank_transfer') {
                cardDetails.style.display = 'none';
                toggleCardFieldsRequired(false);
            } else {
                cardDetails.style.display = 'block';
                toggleCardFieldsRequired(true);
            }
        });
    }
}

// Function to show payment modal for a specific payment
window.showPaymentModal = async function(paymentId) {
    try {
        const payments = JSON.parse(localStorage.getItem('policyholderPayments') || '[]');
        const payment = payments.find(p => p.paymentId === paymentId);

        if (!payment) {
            showErrorNotification('Payment details not found.');
            return;
        }

        // Safely set element values by checking if they exist first
        const paymentIdModal = document.getElementById('payment-id-modal');
        if (paymentIdModal) {
            paymentIdModal.value = payment.paymentId;
        }
        
        const paymentAmountModal = document.getElementById('payment-amount-modal');
        if (paymentAmountModal) {
            paymentAmountModal.value = payment.amount.toFixed(2);
        }
        
        const paymentDueDateModal = document.getElementById('payment-due-date-modal');
        if (paymentDueDateModal) {
            paymentDueDateModal.value = formatDate(payment.dueDate);
        }
        
        // Set selected policy
        const policySelect = document.getElementById('policy-select');
        if (policySelect) {
            // Ensure policies are loaded
            if (policySelect.options.length <= 1) { // Assuming first option is placeholder
                await loadPoliciesForDropdown(policySelect); // Ensure this function exists and is called if needed
            }

            const policyIdToSelect = payment.insurance?.policy?.policyId;
            if (policyIdToSelect) {
                for (let i = 0; i < policySelect.options.length; i++) {
                    if (policySelect.options[i].value == policyIdToSelect) {
                        policySelect.selectedIndex = i;
                        break;
                    }
                }
            }
        }

        const modalElement = document.getElementById('makePaymentModal');
        if (modalElement) {
            const paymentModal = new bootstrap.Modal(modalElement);
            paymentModal.show();
        } else {
            console.warn('Payment modal element not found');
        }
    } catch (error) {
        console.error('Error showing payment modal:', error);
        showErrorNotification('Could not display payment information.');
    }
};

// Function to show receipt modal for a specific payment
window.showReceiptModal = async function(paymentId) {
    try {
        // Fetch receipt using the apiRequest function
        const receipt = await apiRequest(`/api/policyholder/payments/${paymentId}/receipt`, 'GET');
        
        const receiptContent = document.getElementById('receipt-content');
        
        if (receiptContent) {
            receiptContent.innerHTML = `
                <div class="text-center mb-4">
                    <h4>Payment Receipt</h4>
                    <p class="text-muted">Transaction #${receipt.paymentId}</p>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Payment ID:</strong></div>
                    <div class="col-7">${receipt.paymentId}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Policy ID:</strong></div>
                    <div class="col-7">${receipt.policyId}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Amount Paid:</strong></div>
                    <div class="col-7">$${receipt.amount.toLocaleString()}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Payment Date:</strong></div>
                    <div class="col-7">${formatDate(receipt.paymentDate)}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Payment Method:</strong></div>
                    <div class="col-7">${receipt.paymentMethod}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Status:</strong></div>
                    <div class="col-7"><span class="badge bg-success">Paid</span></div>
                </div>
                <hr>
                <div class="text-center mt-3">
                    <p class="mb-1"><small>Thank you for your payment</small></p>
                    <p class="text-muted"><small>This receipt was generated on ${formatDate(receipt.generatedDate)}</small></p>
                </div>
            `;
        }
        
        const modal = new bootstrap.Modal(document.getElementById('receiptModal'));
        modal.show();
        
        // Set up print functionality
        const printReceiptBtn = document.getElementById('print-receipt-btn');
        if (printReceiptBtn) {
            printReceiptBtn.addEventListener('click', function() {
                const receiptWindow = window.open('', '_blank');
                receiptWindow.document.write(`
                    <html>
                    <head>
                        <title>Payment Receipt #${receipt.paymentId}</title>
                        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                        <style>
                            body { padding: 20px; }
                            @media print { 
                                .no-print { display: none; } 
                                body { padding: 0; }
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            ${receiptContent.innerHTML}
                            <div class="text-center mt-4 no-print">
                                <button class="btn btn-primary" onclick="window.print()">Print</button>
                                <button class="btn btn-secondary ms-2" onclick="window.close()">Close</button>
                            </div>
                        </div>
                    </body>
                    </html>
                `);
                receiptWindow.document.close();
            });
        }
    } catch (error) {
        console.error('Error loading receipt:', error);
        showErrorNotification(error.message || 'Failed to load receipt.');
    }
};

// Claims functionality
async function loadClaims() {
    try {
        // Hide any existing error notifications
        const errorNotification = document.getElementById('error-notification');
        if (errorNotification) {
            errorNotification.classList.add('d-none');
        }
        
        console.log('Attempting to load claims from API...');
        // Fetch claims using the apiRequest function with expanded details
        const claims = await apiRequest('/api/policyholder/claims?includeStatusDetails=true', 'GET');
        
        // Update claim statistics
        const totalClaims = document.getElementById('total-claims');
        const approvedClaims = document.getElementById('approved-claims');
        const pendingClaims = document.getElementById('pending-claims');
        const rejectedClaims = document.getElementById('rejected-claims');
        
        // Helper function to get the latest status of a claim
        const getClaimStatus = (claim) => {
            if (!claim.claimStatuses || claim.claimStatuses.length === 0) return 'Unknown';
            
            // Sort by date (newest first) and get the first one
            const sortedStatuses = [...claim.claimStatuses].sort((a, b) => {
                return new Date(b.statusDate) - new Date(a.statusDate);
            });
            
            if (!sortedStatuses[0]) return 'Unknown';
            
            // Check if we have status details from the database
            // First try to use the statusName field we added to the ClaimStatus model
            if (sortedStatuses[0].statusName) {
                return sortedStatuses[0].statusName;
            }
            
            // Fallback to the old way if the new field isn't available
            if (sortedStatuses[0].statusDetails && sortedStatuses[0].statusDetails.statusName) {
                return sortedStatuses[0].statusDetails.statusName;
            }
            
            // Fallback: Use a simple algorithm to determine status based on date
            // This is our robust fallback mechanism when the API doesn't include status details
            const statusDate = new Date(sortedStatuses[0].statusDate);
            const now = new Date();
            const daysDiff = Math.floor((now - statusDate) / (1000 * 60 * 60 * 24));
            
            if (statusDate > now) return 'Pending';
            if (daysDiff <= 30) return 'Processing';
            return 'Completed';
        };
        
        if (totalClaims) totalClaims.textContent = claims.length;
        if (approvedClaims) approvedClaims.textContent = claims.filter(c => getClaimStatus(c) === 'Approved').length;
        if (pendingClaims) pendingClaims.textContent = claims.filter(c => ['Filed', 'Under Review'].includes(getClaimStatus(c))).length;
        if (rejectedClaims) rejectedClaims.textContent = claims.filter(c => getClaimStatus(c) === 'Rejected').length;
        
        // Store claims in localStorage for reference
        localStorage.setItem('policyholderClaims', JSON.stringify(claims));
        
        // Populate claims table
        const claimsTable = document.getElementById('claims-list');
        if (!claimsTable) return;
        
        if (claims.length === 0) {
            claimsTable.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center">No claims found.</td>
                </tr>
            `;
            return;
        }
        
        claimsTable.innerHTML = claims.map(claim => {
            // Get the latest status of the claim
            const status = getClaimStatus(claim);
            
            // Use insuranceId as the policy ID
            const policyId = claim.insuranceId || 'N/A';
            
            // Get status class for badge
            let statusClass = '';
            switch (status) {
                case 'Approved':
                    statusClass = 'bg-success';
                    break;
                case 'Pending':
                    statusClass = 'bg-warning';
                    break;
                case 'Rejected':
                    statusClass = 'bg-danger';
                    break;
                default:
                    statusClass = 'bg-secondary';
            }
            
            return `
                <tr>
                    <td>${claim.claimId}</td>
                    <td>${policyId}</td>
                    <td>${formatDate(claim.claimDate)}</td>
                    <td>${formatDate(claim.incidentDate)}</td>
                    <td>$${claim.amountClaimed ? claim.amountClaimed.toLocaleString() : '0.00'}</td>
                    <td><span class="badge ${statusClass} claim-status-badge">${status}</span></td>
                    <td>
                        <button class="btn btn-outline-primary btn-sm claim-action" onclick="viewClaimDetails(${claim.claimId})">
                            View Details
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
        
    } catch (error) {
        console.error('Error loading claims:', error);
        showErrorNotification('Failed to load claims. Please try again later.');
    }
}

function setupClaimForm() {
    // Populate policy dropdown in claim form
    const policySelect = document.getElementById('claim-policy-select');
    if (!policySelect) return;
    
    // Get policies from localStorage
    const storedPolicies = localStorage.getItem('policyholderPolicies');
    if (storedPolicies) {
        const policies = JSON.parse(storedPolicies);
        
        // Add options to the select
        policies.forEach(policy => {
            const option = document.createElement('option');
            option.value = policy.insuranceId;
            option.textContent = `Policy #${policy.insuranceId} - ${policy.policyType || 'Standard'}`;
            policySelect.appendChild(option);
        });
    }
    
    // Set up claim form submission
    const submitClaimBtn = document.getElementById('submit-claim-btn');
    if (submitClaimBtn) {
        submitClaimBtn.addEventListener('click', async function() {
            const claimForm = document.getElementById('claim-form');
            if (!claimForm.checkValidity()) {
                claimForm.reportValidity();
                return;
            }
            
            try {
                const policyId = document.getElementById('claim-policy-select').value;
                const incidentDate = document.getElementById('incident-date').value;
                const description = document.getElementById('claim-description').value;
                const amount = document.getElementById('claim-amount').value;
                const claimType = document.getElementById('claim-type') ? document.getElementById('claim-type').value : 'General';
                
                const claimData = {
                    policyId,
                    incidentDate,
                    description,
                    amount,
                    claimType
                };
                
                let claimSuccessful = false;
                let responseData = null;
                
                // Use apiRequest with fallback data
                try {
                    responseData = await apiRequest('/api/policyholder/claims', 'POST', claimData);
                    
                    // If we get here, the request was successful
                    claimSuccessful = true;
                    
                } catch (error) {
                    // Even with fallback, something went wrong
                    console.error('Claim submission failed:', error);
                    claimSuccessful = false;
                }
                
                if (claimSuccessful) {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('fileClaimModal'));
                    modal.hide();
                    
                    showSuccessNotification('Claim submitted successfully! Your claim is now under review.');
                    
                    // Reload claims to show the updated list
                    setTimeout(() => {
                        loadClaims();
                    }, 1000);
                } else {
                    throw new Error('Claim submission failed. Please try again.');
                }
            } catch (error) {
                console.error('Claim submission error:', error);
                showErrorNotification(error.message || 'Claim submission failed. Please try again.');
            }
        });
    }
}

// Function to view claim details
window.viewClaimDetails = async function(claimId) {
    try {
        // Fetch claim details using the apiRequest function
        const claim = await apiRequest(`/api/policyholder/claims/${claimId}`, 'GET');
        
        // Show the modal
        const viewClaimModal = new bootstrap.Modal(document.getElementById('viewClaimModal'));
        viewClaimModal.show();
        
        const claimDetailsContent = document.getElementById('claim-details-content');
        
        if (claimDetailsContent) {
            // Define a local getClaimStatus function to ensure it's available in this scope
            const getClaimStatus = (claim) => {
                if (!claim.claimStatuses || claim.claimStatuses.length === 0) return 'Unknown';
                
                // Sort by date (newest first) and get the first one
                const sortedStatuses = [...claim.claimStatuses].sort((a, b) => {
                    return new Date(b.statusDate) - new Date(a.statusDate);
                });
                
                if (!sortedStatuses[0]) return 'Unknown';
                
                // Check if we have status details from the database
                if (sortedStatuses[0].statusDetails && sortedStatuses[0].statusDetails.statusName) {
                    return sortedStatuses[0].statusDetails.statusName;
                }
                
                // Fallback: Use a simple algorithm to determine status based on date
                // This is our robust fallback mechanism when the API doesn't include status details
                const statusDate = new Date(sortedStatuses[0].statusDate);
                const now = new Date();
                const daysDiff = Math.floor((now - statusDate) / (1000 * 60 * 60 * 24));
                
                if (statusDate > now) return 'Pending';
                if (daysDiff <= 30) return 'Processing';
                return 'Completed';
            };
            
            // Get the latest status of the claim
            const status = getClaimStatus(claim);
            
            // Use insuranceId as the policy ID
            const policyId = claim.insuranceId || 'N/A';
            const policyType = 'Insurance Policy'; // Default policy type since it's not in the current data
            
            // Get status class for badge
            let statusClass = '';
            switch (status) {
                case 'Approved':
                    statusClass = 'bg-success';
                    break;
                case 'Pending':
                    statusClass = 'bg-warning';
                    break;
                case 'Rejected':
                    statusClass = 'bg-danger';
                    break;
                default:
                    statusClass = 'bg-secondary';
            }
            
            // Format documents list if available
            let documentsList = '';
            if (claim.claimDocuments && claim.claimDocuments.length > 0) {
                documentsList = claim.claimDocuments.map(doc => `<li>${doc.documentName} (${formatDate(doc.uploadDate)})</li>`).join('');
            } else {
                documentsList = '<li>No documents available</li>';
            }
            
            // Get status details
            let statusDetails = '';
            if (claim.claimStatuses && claim.claimStatuses.length > 0) {
                // Sort by date (newest first)
                const sortedStatuses = [...claim.claimStatuses].sort((a, b) => {
                    return new Date(b.statusDate) - new Date(a.statusDate);
                });
                
                const latestStatus = sortedStatuses[0];
                
                // Add additional status details based on the status
                if (status === 'Approved' && latestStatus.amount) {
                    statusDetails = `<div class="row mb-2">
                        <div class="col-5"><strong>Approved Amount:</strong></div>
                        <div class="col-7">$${latestStatus.amount.toLocaleString()}</div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-5"><strong>Approval Date:</strong></div>
                        <div class="col-7">${formatDate(latestStatus.statusDate)}</div>
                    </div>`;
                } else if (status === 'Rejected' && latestStatus.notes) {
                    statusDetails = `<div class="row mb-2">
                        <div class="col-5"><strong>Rejection Reason:</strong></div>
                        <div class="col-7">${latestStatus.notes}</div>
                    </div>
                    <div class="row mb-2">
                        <div class="col-5"><strong>Rejection Date:</strong></div>
                        <div class="col-7">${formatDate(latestStatus.statusDate)}</div>
                    </div>`;
                }
            }
            
            claimDetailsContent.innerHTML = `
                <div class="text-center mb-4">
                    <h4>Claim #${claim.claimId}</h4>
                    <span class="badge ${statusClass}">${status}</span>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Claim ID:</strong></div>
                    <div class="col-7">${claim.claimId}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Policy ID:</strong></div>
                    <div class="col-7">${policyId}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Policy Type:</strong></div>
                    <div class="col-7">${policyType}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Claim Type:</strong></div>
                    <div class="col-7">${claim.claimType || 'General'}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Claim Date:</strong></div>
                    <div class="col-7">${formatDate(claim.claimDate)}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Incident Date:</strong></div>
                    <div class="col-7">${formatDate(claim.incidentDate)}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Claim Amount:</strong></div>
                    <div class="col-7">$${claim.amountClaimed ? claim.amountClaimed.toLocaleString() : '0.00'}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Description:</strong></div>
                    <div class="col-7">${claim.description}</div>
                </div>
                <div class="row mb-2">
                    <div class="col-5"><strong>Status:</strong></div>
                    <div class="col-7"><span class="badge ${statusClass}">${status}</span></div>
                </div>
                ${statusDetails}
                <div class="row mb-2">
                    <div class="col-5"><strong>Documents:</strong></div>
                    <div class="col-7">
                        <ul class="mb-0">
                            ${documentsList}
                        </ul>
                    </div>
                </div>
                <hr>
                <div class="text-center mt-3">
                    <p class="text-muted"><small>Last updated on ${formatDate(claim.lastUpdated || new Date())}</small></p>
                </div>
            `;
        }
        
        // Set up print button
        const printClaimBtn = document.getElementById('print-claim-btn');
        if (printClaimBtn) {
            printClaimBtn.onclick = function() {
                const printWindow = window.open('', '_blank');
                printWindow.document.write(`
                    <html>
                        <head>
                            <title>Claim #${claim.claimId} Details</title>
                            <style>
                                body { font-family: Arial, sans-serif; line-height: 1.6; }
                                .container { max-width: 800px; margin: 0 auto; padding: 20px; }
                                .header { text-align: center; margin-bottom: 20px; }
                                .row { display: flex; margin-bottom: 10px; }
                                .col-5 { width: 40%; font-weight: bold; }
                                .col-7 { width: 60%; }
                                .badge { display: inline-block; padding: 5px 10px; border-radius: 4px; color: white; }
                                .bg-success { background-color: #28a745; }
                                .bg-warning { background-color: #ffc107; color: #212529; }
                                .bg-danger { background-color: #dc3545; }
                                .bg-secondary { background-color: #6c757d; }
                                ul { padding-left: 20px; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <h2>Claim #${claim.claimId} Details</h2>
                                    <div class="badge ${statusClass}">${status}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Claim ID:</div>
                                    <div class="col-7">${claim.claimId}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Policy ID:</div>
                                    <div class="col-7">${policyId}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Policy Type:</div>
                                    <div class="col-7">${policyType}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Claim Type:</div>
                                    <div class="col-7">${claim.claimType || 'General'}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Claim Date:</div>
                                    <div class="col-7">${formatDate(claim.claimDate)}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Incident Date:</div>
                                    <div class="col-7">${formatDate(claim.incidentDate)}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Claim Amount:</div>
                                    <div class="col-7">$${claim.amount.toLocaleString()}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Description:</div>
                                    <div class="col-7">${claim.description}</div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Status:</div>
                                    <div class="col-7"><span class="badge ${statusClass}">${status}</span></div>
                                </div>
                                <!-- Print status details if available -->
                                ${statusDetails.replace(/\n/g, '')}
                                <div class="row">
                                    <div class="col-5">Documents:</div>
                                    <div class="col-7">
                                        <ul>
                                            ${documentsList}
                                        </ul>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-5">Printed On:</div>
                                    <div class="col-7">${formatDate(new Date().toISOString())}</div>
                                </div>
                            </div>
                        </body>
                    </html>
                `);
                printWindow.document.close();
                printWindow.focus();
                setTimeout(() => {
                    printWindow.print();
                    printWindow.close();
                }, 250);
            };
        }
    } catch (error) {
        console.error('Error viewing claim details:', error);
        showErrorNotification('Failed to load claim details');
    }
};
// Include the claim-status.js helper functions
