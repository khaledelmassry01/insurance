/**
 * Claim Status Helper Functions
 * This file contains helper functions for handling claim statuses
 */

/**
 * Get the appropriate CSS class for a claim status
 * @param {string} status - The status name
 * @returns {string} The CSS class for the status badge
 */
function getStatusClass(status) {
    if (!status) return 'bg-secondary';
    
    // Convert to lowercase for case-insensitive comparison
    const statusLower = status.toLowerCase();
    
    if (statusLower === 'approved') return 'bg-success';
    if (statusLower === 'filed' || statusLower === 'under review' || statusLower === 'pending') return 'bg-warning';
    if (statusLower === 'rejected') return 'bg-danger';
    if (statusLower === 'closed') return 'bg-info';
    
    return 'bg-secondary'; // Default fallback
}

/**
 * Get the latest status of a claim
 * @param {Object} claim - The claim object
 * @returns {string} The status name
 */
function getClaimStatus(claim) {
    // Check if claim has statuses
    if (!claim || !claim.claimStatuses || !Array.isArray(claim.claimStatuses) || claim.claimStatuses.length === 0) {
        return 'Unknown';
    }
    
    // Sort statuses by date (newest first)
    const sortedStatuses = [...claim.claimStatuses].sort((a, b) => {
        return new Date(b.statusDate) - new Date(a.statusDate);
    });
    
    if (!sortedStatuses[0]) return 'Unknown';
    
    // First try to use the statusName field we added to the ClaimStatus model
    if (sortedStatuses[0].statusName) {
        return sortedStatuses[0].statusName;
    }
    
    // Next try to use the statusDetails if available
    if (sortedStatuses[0].statusDetails && sortedStatuses[0].statusDetails.statusName) {
        return sortedStatuses[0].statusDetails.statusName;
    }
    
    // Finally, try to use the statusId if available
    if (sortedStatuses[0].statusId) {
        // Map status IDs to names based on the database values
        switch (sortedStatuses[0].statusId) {
            case 1: return 'Filed';
            case 2: return 'Under Review';
            case 3: return 'Approved';
            case 4: return 'Rejected';
            case 5: return 'Closed';
            default: return 'Unknown';
        }
    }
    
    // Fallback: Use a simple algorithm to determine status based on date
    // This is our robust fallback mechanism when the API doesn't include status details
    const statusDate = new Date(sortedStatuses[0].statusDate);
    const now = new Date();
    const daysDiff = Math.floor((now - statusDate) / (1000 * 60 * 60 * 24));
    
    if (statusDate > now) return 'Pending';
    if (daysDiff <= 30) return 'Under Review';
    return 'Closed';
}
