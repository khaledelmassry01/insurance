const ValidationRules = {
    required: {
        test: value => value !== null && value !== undefined && value.toString().trim() !== '',
        message: 'This field is required'
    },
    email: {
        test: value => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
        message: 'Please enter a valid email address'
    },
    phone: {
        test: value => /^[\d\s\-\(\)\+]{7,20}$/.test(value),
        message: 'Please enter a valid phone number'
    },
    date: {
        test: value => !isNaN(Date.parse(value)),
        message: 'Please enter a valid date'
    },
    number: {
        test: value => !isNaN(parseFloat(value)) && isFinite(value),
        message: 'Please enter a valid number'
    },
    integer: {
        test: value => Number.isInteger(Number(value)),
        message: 'Please enter a valid integer'
    },
    minLength: (min) => ({
        test: value => value.length >= min,
        message: `Must be at least ${min} characters`
    }),
    maxLength: (max) => ({
        test: value => value.length <= max,
        message: `Must be no more than ${max} characters`
    }),
    min: (min) => ({
        test: value => parseFloat(value) >= min,
        message: `Must be at least ${min}`
    }),
    max: (max) => ({
        test: value => parseFloat(value) <= max,
        message: `Must be no more than ${max}`
    }),
    password: {
        test: value => /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/.test(value),
        message: 'Password must be at least 8 characters with uppercase, lowercase and numbers'
    },
    zipCode: {
        test: value => /^\d{5}(-\d{4})?$/.test(value),
        message: 'Please enter a valid ZIP code'
    },
    futureDate: {
        test: value => {
            const inputDate = new Date(value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            return inputDate >= today;
        },
        message: 'Date must be in the future'
    },
    pastDate: {
        test: value => {
            const inputDate = new Date(value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            return inputDate <= today;
        },
        message: 'Date must be in the past'
    },
    match: (field, fieldName) => ({
        test: (value, formData) => value === formData.get(field),
        message: `Must match ${fieldName}`
    })
};

function validateElement(element, formData) {
    const value = element.value;
    const rules = element.dataset.validate ? element.dataset.validate.split(' ') : [];
    
    if (element.style.display === 'none' || element.disabled) {
        return { isValid: true, message: '' };
    }
    
    if ((element.required || rules.includes('required')) && !ValidationRules.required.test(value)) {
        return { isValid: false, message: ValidationRules.required.message };
    }
    
    if (value === '' && !element.required && !rules.includes('required')) {
        return { isValid: true, message: '' };
    }
    
    for (const rule of rules) {
        if (rule === 'required') continue;
        
        if (rule.includes(':')) {
            const [ruleName, param] = rule.split(':');
            const validationRule = ValidationRules[ruleName](parseInt(param, 10));
            if (!validationRule.test(value, formData)) {
                return { isValid: false, message: validationRule.message };
            }
        } 
        else if (rule.startsWith('match:')) {
            const [, field, fieldName] = rule.split(':');
            const validationRule = ValidationRules.match(field, fieldName);
            if (!validationRule.test(value, formData)) {
                return { isValid: false, message: validationRule.message };
            }
        }
        else if (ValidationRules[rule]) {
            if (!ValidationRules[rule].test(value, formData)) {
                return { isValid: false, message: ValidationRules[rule].message };
            }
        }
    }
    
    switch (element.type) {
        case 'email':
            if (!ValidationRules.email.test(value)) {
                return { isValid: false, message: ValidationRules.email.message };
            }
            break;
        case 'tel':
            if (!ValidationRules.phone.test(value)) {
                return { isValid: false, message: ValidationRules.phone.message };
            }
            break;
        case 'number':
            if (!ValidationRules.number.test(value)) {
                return { isValid: false, message: ValidationRules.number.message };
            }
            break;
        case 'date':
            if (!ValidationRules.date.test(value)) {
                return { isValid: false, message: ValidationRules.date.message };
            }
            break;
    }
    
    return { isValid: true, message: '' };
}

function showValidationError(element, message) {
    removeValidationError(element);
    
    element.classList.add('is-invalid');
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.textContent = message;
    
    if (element.parentNode) {
        element.parentNode.insertBefore(errorDiv, element.nextSibling);
    }
}

function removeValidationError(element) {
    element.classList.remove('is-invalid');
    
    const nextSibling = element.nextSibling;
    if (nextSibling && nextSibling.className === 'invalid-feedback') {
        nextSibling.parentNode.removeChild(nextSibling);
    }
}

function validateForm(form) {
    const formData = new FormData(form);
    const formElements = form.querySelectorAll('input, select, textarea');
    let isValid = true;
    
    formElements.forEach(element => {
        const result = validateElement(element, formData);
        if (!result.isValid) {
            showValidationError(element, result.message);
            isValid = false;
        } else {
            removeValidationError(element);
        }
    });
    
    return isValid;
}

function setupFormValidation(form) {
    const formElements = form.querySelectorAll('input, select, textarea');
    
    formElements.forEach(element => {
        element.addEventListener('blur', () => {
            const result = validateElement(element, new FormData(form));
            if (!result.isValid) {
                showValidationError(element, result.message);
            } else {
                removeValidationError(element);
            }
        });
        
        if (element.tagName === 'SELECT') {
            element.addEventListener('change', () => {
                const result = validateElement(element, new FormData(form));
                if (!result.isValid) {
                    showValidationError(element, result.message);
                } else {
                    removeValidationError(element);
                }
            });
        }
    });
    
    form.addEventListener('submit', event => {
        if (!validateForm(form)) {
            event.preventDefault();
            event.stopPropagation();
            
            if (typeof showError === 'function') {
                showError('Please correct the errors in the form before submitting.');
            }
            
            const firstInvalid = form.querySelector('.is-invalid');
            if (firstInvalid) {
                firstInvalid.focus();
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('form[data-validate="true"]').forEach(form => {
        setupFormValidation(form);
    });
});

window.ValidationUtils = {
    validateForm,
    setupFormValidation,
    validateElement,
    showValidationError,
    removeValidationError
};
