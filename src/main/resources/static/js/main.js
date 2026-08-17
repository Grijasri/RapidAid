// RapidAid Application Client JavaScript

document.addEventListener("DOMContentLoaded", function () {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 5000);
    });

    // Confirmation Prompts for Delete Actions
    const deleteButtons = document.querySelectorAll('.btn-delete-confirm');
    deleteButtons.forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            const message = this.getAttribute('data-confirm-message') || 'Are you sure you want to delete this record? This action cannot be undone.';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
});
