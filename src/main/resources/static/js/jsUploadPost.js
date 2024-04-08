document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('check').addEventListener('change', function() {
        var privacyInput = document.getElementById('privacyInput');
        if (this.checked) {
            privacyInput.value = 'private';
        } else {
            privacyInput.value = 'public';
        }
    });

    if (check.checked) {
        privacyInput.value = 'private';
    } else {
        privacyInput.value = 'public';
    }
});