document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (token) {
        updateDashboardUI();
        setTimeout(() => {
            fetchDashboardData();
        }, 100);
    }
    
    // Listen for stats-updated events from other components
    window.addEventListener('stats-updated', (event) => {
        console.log('Received stats update event, updating dashboard UI');
        const data = event.detail;
        updateDashboardWithData(data);
    });
});

// Helper function to update dashboard with data
function updateDashboardWithData(data) {
    console.log('Updating dashboard with data:', data);
    const policyCountElement = document.getElementById('policyCount');
    const policyholderCountElement = document.getElementById('policyholderCount');
    const reportCountElement = document.getElementById('reportCount');
    
    if (policyCountElement) {
        policyCountElement.textContent = (data.activePolicies !== undefined ? data.activePolicies : 0) + ' Policies';
    }
    if (policyholderCountElement) {
        policyholderCountElement.textContent = (data.totalPolicyholders !== undefined ? data.totalPolicyholders : 0) + ' Policyholders';
    }
    if (reportCountElement) {
        reportCountElement.textContent = (data.totalClaims !== undefined ? data.totalClaims : 0) + ' Financial Requested';
    }
}

function updateDashboardUI() {
    document.getElementById('navLinks').style.display = 'flex';
    document.getElementById('loginSection').style.display = 'none';
    document.getElementById('welcomeSection').style.display = 'block';
    const agent = JSON.parse(localStorage.getItem('agent'));
    const userFullNameElement = document.getElementById('userFullName');
    if (agent && agent.firstName && agent.lastName) {
        userFullNameElement.textContent = `${agent.firstName} ${agent.lastName}`;
    } else {
        userFullNameElement.textContent = 'Agent';
    }
}

async function fetchDashboardData(forceRefresh = false) {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('No token found when trying to fetch dashboard data');
            return;
        }
        
        // If the centralized function isn't available, fall back to direct API call
        let data;
        if (window.getDashboardStats) {
            console.log('Using centralized stats function');
            data = await window.getDashboardStats(forceRefresh);
        } else {
            console.log('Centralized stats function not available, making direct API call');
            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };
            const response = await fetch('/api/dashboard/stats', {
                method: 'GET',
                headers: headers
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            data = await response.json();
        }
        
        console.log('Dashboard data received:', data);
        
        const policyCountElement = document.getElementById('policyCount');
        const policyholderCountElement = document.getElementById('policyholderCount');
        const reportCountElement = document.getElementById('reportCount');
        
        if (policyCountElement) {
            policyCountElement.textContent = (data.activePolicies !== undefined ? data.activePolicies : 0) + ' Policies';
        }
        if (policyholderCountElement) {
            policyholderCountElement.textContent = (data.totalPolicyholders !== undefined ? data.totalPolicyholders : 0) + ' Policyholders';
        }
        if (reportCountElement) {
            reportCountElement.textContent = (data.totalClaims !== undefined ? data.totalClaims : 0) + ' Financial Requested';
        }

    } catch (error) {
        console.error('Error fetching dashboard data:', error);
        
        const policyCountElement = document.getElementById('policyCount');
        const policyholderCountElement = document.getElementById('policyholderCount');
        const reportCountElement = document.getElementById('reportCount');
        
        if (policyCountElement) {
            policyCountElement.textContent = 'Error loading data';
        }
        if (policyholderCountElement) {
            policyholderCountElement.textContent = 'Error loading data';
        }
        if (reportCountElement) {
            reportCountElement.textContent = 'Error loading data';
        }
    }
}

window.addEventListener('auth-state-changed', () => {
    const token = localStorage.getItem('token');
    if (token) {
        updateDashboardUI();
        fetchDashboardData();
    } else {
        document.getElementById('navLinks').style.display = 'none';
        document.getElementById('welcomeSection').style.display = 'none';
        document.getElementById('loginSection').style.display = 'block';
    }
});

window.refreshDashboard = function() {
    // Force refresh when manually refreshing
    fetchDashboardData(true);
};
