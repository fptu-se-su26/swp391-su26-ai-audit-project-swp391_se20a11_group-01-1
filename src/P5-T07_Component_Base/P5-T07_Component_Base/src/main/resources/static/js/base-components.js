function openBaseModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.add('show');
    }
}

function closeBaseModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.remove('show');
    }
}

document.addEventListener('click', function (event) {
    const closeTarget = event.target.getAttribute('data-modal-close');
    if (closeTarget) {
        closeBaseModal(closeTarget);
    }

    if (event.target.classList.contains('modal-backdrop')) {
        event.target.classList.remove('show');
    }
});

document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        document.querySelectorAll('.modal-backdrop.show').forEach(function (modal) {
            modal.classList.remove('show');
        });
    }
});
