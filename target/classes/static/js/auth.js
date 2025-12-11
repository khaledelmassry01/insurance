document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const loginSection = document.getElementById('loginSection');
    const registerSection = document.getElementById('registerSection');
    const welcomeSection = document.getElementById('welcomeSection');
    const navLinks = document.getElementById('navLinks');
    const showRegisterLink = document.getElementById('showRegister');
    const backToLoginBtn = document.getElementById('backToLogin');
    const showPolicyholderRegisterLink = document.getElementById('showPolicyholderRegister');

    function showSection(section) {
        // Hide all sections by adding d-none class
        if (loginSection) {
            loginSection.classList.remove('d-block');
            loginSection.classList.add('d-none');
        }
        if (registerSection) {
            registerSection.classList.remove('d-block');
            registerSection.classList.add('d-none');
        }
        if (welcomeSection) {
            welcomeSection.classList.remove('d-block');
            welcomeSection.classList.add('d-none');
        }
        
        // Show the selected section
        if (section) {
            section.classList.remove('d-none');
            section.classList.add('d-block');
        }
    }

    if (showRegisterLink) {
        showRegisterLink.addEventListener('click', (e) => {
            e.preventDefault();
            showSection(registerSection);
        });
    }

    if (backToLoginBtn) {
        backToLoginBtn.addEventListener('click', () => {
            showSection(loginSection);
        });
    }

    if (showPolicyholderRegisterLink) {
        showPolicyholderRegisterLink.addEventListener('click', (e) => {
            e.preventDefault();
            alert('Add Policyholder Comming Soon !');
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const errorDiv = document.getElementById('loginError');
            
            // Clear previous error messages
            if (errorDiv) {
                errorDiv.classList.add('d-none');
            }
            
            // Validate email format
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                if (errorDiv) {
                    errorDiv.textContent = 'Please enter a valid email address';
                    errorDiv.classList.remove('d-none');
                }
                return;
            }
            
            // Validate password (at least 6 characters)
            if (password.length < 6) {
                if (errorDiv) {
                    errorDiv.textContent = 'Password must be at least 6 characters';
                    errorDiv.classList.remove('d-none');
                }
                return;
            }

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password })
                });

                if (response.ok) {
                    const data = await response.json();

                    localStorage.setItem('token', data.token);
                    localStorage.setItem('agent', JSON.stringify(data.agent));
                    localStorage.setItem('isLoggedIn', 'true');

                    // Mark body as authenticated
                    document.body.classList.add('authenticated');

                    showSection(welcomeSection);
                    if (navLinks) navLinks.classList.remove('nav-hidden');
                    const userFullName = document.getElementById('userFullName');
                    if (userFullName) userFullName.textContent = `${data.agent.firstName} ${data.agent.lastName}`;
                    
                    // Dispatch event to trigger dashboard data loading
                    window.dispatchEvent(new Event('auth-state-changed'));

                    loginForm.reset();
                    const loginError = document.getElementById('loginError');
                    if (loginError) loginError.classList.add('d-none');
                    
                    showSuccess('Login successful!');
                } else {
                    try {
                        // Try to parse the error response as JSON
                        const errorData = await response.json();
                        const errorDiv = document.getElementById('loginError');
                        
                        // Extract a user-friendly message from the error response
                        let errorMessage = 'Login failed. Please check your credentials.';
                        if (errorData.message) {
                            errorMessage = errorData.message;
                        }
                        
                        if (errorDiv) {
                            errorDiv.textContent = errorMessage;
                            errorDiv.classList.remove('d-none');
                        }
                    } catch (parseError) {
                        // If the response is not JSON, use the text directly
                        const errorText = await response.text();
                        const errorDiv = document.getElementById('loginError');
                        if (errorDiv) {
                            errorDiv.textContent = 'Login failed. Please check your credentials.';
                            errorDiv.classList.remove('d-none');
                        }
                        console.error('Error parsing response:', parseError, 'Response was:', errorText);
                    }
                }
            } catch (error) {
                console.error('Error:', error);
                const errorDiv = document.getElementById('loginError');
                if (errorDiv) {
                    errorDiv.textContent = 'An error occurred. Please try again.';
                    errorDiv.classList.remove('d-none');
                }
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const password = document.getElementById('register-password').value;
            const confirmPassword = document.getElementById('register-confirmPassword').value;

        if (password !== confirmPassword) {
            const errorDiv = document.getElementById('registerError');
            errorDiv.textContent = 'Passwords do not match';
            errorDiv.classList.remove('d-none');
            return;
        }

        const formData = {
            firstName: document.getElementById('register-firstName').value,
            lastName: document.getElementById('register-lastName').value,
            licenseNumber: document.getElementById('licenseNumber').value,
            gender: document.getElementById('gender').value,
            email: document.getElementById('register-email').value,
            phoneNumber: document.getElementById('phoneNumber').value,
            password: password,
            state: document.getElementById('state').value,
            city: document.getElementById('city').value,
            street: document.getElementById('street').value,
            zipCode: document.getElementById('zipCode').value,
            role: 'AGENT' // Default role for registration
        };

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                showSection(loginSection);
                const successDiv = document.createElement('div');
                successDiv.className = 'alert alert-success';
                successDiv.textContent = 'Registration successful! Please login with your credentials.';
                loginForm.insertBefore(successDiv, loginForm.firstChild);
                setTimeout(() => successDiv.remove(), 5000);

                registerForm.reset();
                const regError = document.getElementById('registerError');
                if (regError) regError.classList.add('d-none');
                showSuccess('Registration successful! Please login with your credentials.');
            } else {
                try {
                    // Try to parse the error response as JSON
                    const errorData = await response.json();
                    const errorDiv = document.getElementById('registerError');
                    
                    // Extract a user-friendly message from the error response
                    let errorMessage = 'Registration failed. Please try again.';
                    if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                    
                    if (errorDiv) {
                        errorDiv.textContent = errorMessage;
                        errorDiv.classList.remove('d-none');
                    }
                } catch (parseError) {
                    // If the response is not JSON, use the text directly
                    const errorText = await response.text();
                    const errorDiv = document.getElementById('registerError');
                    if (errorDiv) {
                        errorDiv.textContent = 'Registration failed. Please try again.';
                        errorDiv.classList.remove('d-none');
                    }
                    console.error('Error parsing response:', parseError, 'Response was:', errorText);
                }
            }
        } catch (error) {
            console.error('Error:', error);
            const errorDiv = document.getElementById('registerError');
            if (errorDiv) {
                errorDiv.textContent = 'An error occurred. Please try again.';
                errorDiv.classList.remove('d-none');
            }
        }
    });
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function (e) {
            e.preventDefault();
            try {
                const response = await fetch('/api/auth/logout', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });

                if (response.ok) {
                    document.body.classList.remove('authenticated');
                    localStorage.removeItem('token');
                    localStorage.removeItem('agent');
                    localStorage.removeItem('isLoggedIn');
                    
                    if (loginSection) {
                        showSection(loginSection);
                    } else {
                        // If not on index page, redirect to login
                        window.location.href = '/index.html';
                    }
                    if (navLinks) navLinks.classList.add('nav-hidden');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        });
    }

    // Restore session
    const token = localStorage.getItem('token');
    if (token) {
        // Verify token
        fetch('/api/auth/verify', {
            headers: { 'Authorization': `Bearer ${token}` }
        })
        .then(response => {
            if (response.ok) {
                // Mark body as authenticated for pages that need it
                document.body.classList.add('authenticated');
                
                const agent = JSON.parse(localStorage.getItem('agent'));
                if (welcomeSection) {
                    showSection(welcomeSection);
                }
                if (navLinks) navLinks.classList.remove('nav-hidden');
                
                const userFullName = document.getElementById('userFullName');
                if (userFullName && agent) {
                    userFullName.textContent = `${agent.firstName} ${agent.lastName}`;
                }
                
                // Dispatch event to trigger dashboard data loading
                window.dispatchEvent(new Event('auth-state-changed'));
            } else {
                // Token is invalid or expired
                document.body.classList.remove('authenticated');
                localStorage.removeItem('token');
                localStorage.removeItem('agent');
                localStorage.removeItem('isLoggedIn');
                if (loginSection) {
                    showSection(loginSection);
                } else {
                    // If not on index page, redirect to login
                    window.location.href = '/index.html';
                }
                if (navLinks) navLinks.classList.add('nav-hidden');
            }
        })
        .catch(error => {
            console.error('Error verifying token:', error);
            document.body.classList.remove('authenticated');
            localStorage.removeItem('token');
            localStorage.removeItem('agent');
            localStorage.removeItem('isLoggedIn');
            if (loginSection) {
                showSection(loginSection);
            } else {
                // If not on index page, redirect to login
                window.location.href = '/index.html';
            }
            if (navLinks) navLinks.classList.add('nav-hidden');
        });
    } else {
        // No token found
        document.body.classList.remove('authenticated');
        if (!loginSection) {
            // If not on index page and no token, redirect to login
            window.location.href = '/index.html';
        }
    }
});
