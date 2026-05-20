document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-confirm]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (!confirm(form.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });

    const imageInput = document.querySelector("[data-image-input]");
    const imagePreview = document.querySelector("[data-image-preview]");

    if (imageInput && imagePreview) {
        imageInput.addEventListener("change", () => {
            const file = imageInput.files[0];

            if (!file) {
                imagePreview.hidden = true;
                imagePreview.removeAttribute("src");
                return;
            }

            imagePreview.src = URL.createObjectURL(file);
            imagePreview.hidden = false;
        });
    }
});