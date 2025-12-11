// API request for admin/agent functions
async function apiRequest(url, options = {}) {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/index.html';
        return Promise.reject('No token found');
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        ...options.headers
    };

    try {
        const response = await fetch(url, {
            ...options,
            headers
        });

        if (response.status === 401 || response.status === 403) {
            // Token
            localStorage.removeItem('token');
            localStorage.removeItem('agent');
            localStorage.removeItem('isLoggedIn');
            window.location.href = '/index.html';
            return Promise.reject('Unauthorized');
        }

        if (!response.ok) {
            const error = await response.text();
            return Promise.reject(error);
        }

        return response.json();
    } catch (error) {
        console.error('API request failed:', error);
        return Promise.reject(error);
    }
}

// API request for policyholder functions with fallback support
async function policyholderApiRequest(url, options = {}, fallbackData = null) {
    // Check if we're in development mode (localhost)
    const isDevMode = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    
    // Flag to track if we're using fallback data
    let usingFallback = false;
    
    const token = localStorage.getItem('policyholderToken');
    if (!token) {
        window.location.href = '/policyholder-login.html';
        return Promise.reject('No token found');
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        ...options.headers
    };

    try {
        // Always attempt to fetch real data from the API first
        try {
            console.log(`Attempting to fetch data from ${url}...`);
            const response = await fetch(url, {
                ...options,
                headers
            });

            if (response.status === 401 || response.status === 403) {
                // Token expired or invalid
                console.warn(`Authentication error (${response.status}) for ${url}`);
                localStorage.removeItem('policyholderToken');
                localStorage.removeItem('policyholderData');
                window.location.href = '/policyholder-login.html';
                return Promise.reject('Unauthorized');
            }

            if (!response.ok) {
                // If fallback data is provided, use it regardless of development mode
                // This ensures we always have data to display
                if (fallbackData !== null) {
                    console.warn(`API returned ${response.status} for ${url}, using fallback data`);
                    // Try to get more details about the error
                    try {
                        const errorText = await response.text();
                        console.error(`API error details: ${errorText}`);
                    } catch (e) {
                        console.error('Could not read error details');
                    }
                    usingFallback = true;
                } else {
                    const error = await response.text();
                    return Promise.reject(error);
                }
            } else {
                // API call succeeded, return the data
                const data = await response.json();
                console.log(`Successfully fetched real data from ${url}`);
                return data;
            }
        } catch (fetchError) {
            // Network or other fetch error
            if (fallbackData !== null) {
                console.warn(`API request failed for ${url}, using fallback data:`, fetchError);
                usingFallback = true;
            } else {
                console.error('Policyholder API request failed:', fetchError);
                return Promise.reject(fetchError);
            }
        }
        
        // If we get here and usingFallback is true, return the fallback data
        if (usingFallback) {
            console.warn(`Using fallback data for ${url} as real API call failed`);
            // Add a special property to indicate this is fallback data
            if (typeof fallbackData === 'object' && fallbackData !== null) {
                // For arrays, add the property to each item
                if (Array.isArray(fallbackData)) {
                    fallbackData = fallbackData.map(item => {
                        if (typeof item === 'object' && item !== null) {
                            return { ...item, _isFallbackData: true };
                        }
                        return item;
                    });
                } else {
                    // For objects, add the property directly
                    fallbackData = { ...fallbackData, _isFallbackData: true };
                }
            }
            return fallbackData;
        }
        
        // If we get here without returning, something went wrong
        return Promise.reject('API request failed and no fallback data was provided');
    } catch (error) {
        console.error('Unexpected error in policyholderApiRequest:', error);
        // Always use fallback data if available, even for unexpected errors
        if (fallbackData !== null) {
            console.warn('Using fallback data due to unexpected error');
            return fallbackData;
        }
        return Promise.reject(error);
    }
}

