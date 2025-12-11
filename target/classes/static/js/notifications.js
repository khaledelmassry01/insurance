function ensureNotificationContainer() {
    if (!document.getElementById('notification-container')) {
        const container = document.createElement('div');
        container.id = 'notification-container';
        container.className = 'position-fixed top-0 start-50 translate-middle-x p-3';
        container.style.zIndex = '1050';
        container.style.width = '400px';
        container.style.maxWidth = '90%';
        document.body.appendChild(container);
    }
    return document.getElementById('notification-container');
}

function showNotification(message, type = 'info', duration = 3000) {
    const container = ensureNotificationContainer();
    const notificationId = 'notification-' + Date.now();
    
    let bgClass = 'bg-info';
    let titleText = 'Information';
    switch (type) {
        case 'success': 
            bgClass = 'bg-success'; 
            titleText = 'Success';
            break;
        case 'error': 
            bgClass = 'bg-danger'; 
            titleText = 'Error';
            break;
        case 'warning': 
            bgClass = 'bg-warning'; 
            titleText = 'Warning';
            break;
        case 'info': 
            bgClass = 'bg-info'; 
            titleText = 'Information';
            break;
    }
    
    const notificationHtml = `
        <div id="${notificationId}" class="alert ${bgClass} text-white shadow mb-3" role="alert">
            <div class="d-flex justify-content-between align-items-center">
                <strong>${titleText}</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <div class="mt-2">
                ${message}
            </div>
        </div>
    `;
    
    container.insertAdjacentHTML('beforeend', notificationHtml);
    
    const notificationElement = document.getElementById(notificationId);
    setTimeout(() => {
        if (notificationElement) {
            notificationElement.classList.add('fade');
            setTimeout(() => {
                if (notificationElement && notificationElement.parentNode) {
                    notificationElement.parentNode.removeChild(notificationElement);
                }
            }, 500);
        }
    }, duration);
    
    const closeButton = notificationElement.querySelector('.btn-close');
    if (closeButton) {
        closeButton.addEventListener('click', () => {
            if (notificationElement && notificationElement.parentNode) {
                notificationElement.parentNode.removeChild(notificationElement);
            }
        });
    }
}

function showSuccess(message, duration = 3000) {
    showNotification(message, 'success', duration);
}

function showError(message, duration = 4000) {
    showNotification(message, 'error', duration);
}

function showWarning(message, duration = 3500) {
    showNotification(message, 'warning', duration);
}

function showInfo(message, duration = 3000) {
    showNotification(message, 'info', duration);
}

function confirmAction(message) {
    return new Promise((resolve) => {
        let confirmModal = document.getElementById('confirm-action-modal');
        if (!confirmModal) {
            const modalHtml = `
                <div class="modal fade" id="confirm-action-modal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header bg-warning text-white">
                                <h5 class="modal-title" id="confirmModalLabel">Confirm Action</h5>
                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body" id="confirm-modal-message">
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="confirm-cancel-btn">Cancel</button>
                                <button type="button" class="btn btn-primary" id="confirm-ok-btn">Confirm</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            document.body.insertAdjacentHTML('beforeend', modalHtml);
            confirmModal = document.getElementById('confirm-action-modal');
        }
        
        document.getElementById('confirm-modal-message').textContent = message;
        
        const modal = new bootstrap.Modal(confirmModal);
        
        const handleConfirm = () => {
            modal.hide();
            resolve(true);
            cleanup();
        };
        
        const handleCancel = () => {
            modal.hide();
            resolve(false);
            cleanup();
        };
        
        const cleanup = () => {
            document.getElementById('confirm-ok-btn').removeEventListener('click', handleConfirm);
            document.getElementById('confirm-cancel-btn').removeEventListener('click', handleCancel);
            confirmModal.removeEventListener('hidden.bs.modal', handleCancel);
        };
        
        document.getElementById('confirm-ok-btn').addEventListener('click', handleConfirm);
        document.getElementById('confirm-cancel-btn').addEventListener('click', handleCancel);
        confirmModal.addEventListener('hidden.bs.modal', handleCancel);
        
        modal.show();
    });
}
