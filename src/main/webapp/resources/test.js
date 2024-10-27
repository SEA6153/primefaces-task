/*
 * This JavaScript code manages a user interface that allows toggling between light and dark themes,
 * as well as handling dialog interactions for adding new entries (e.g., music data).
 * The isDarkMode variable tracks the current theme state. The showAlert function displays simple alert messages.
 * The toggleTheme function switches between light and dark modes by toggling the 'dark-mode' class on the body element,
 * and it updates the theme toggle button's icon accordingly using the updateThemeButtonIcon function.
 * The script also initializes the theme button icon when the document content is loaded.
 * Functions for managing dialog visibility, form validation, dialog title styling, and input field clearing
 * enhance user experience by ensuring data integrity and visual coherence.
 */

let isDarkMode = false;


function toggleTheme() {
    document.body.classList.toggle('dark-mode');
    isDarkMode = !isDarkMode;


    setTimeout(updateThemeButtonIcon, 10);
}

function updateThemeButtonIcon() {
    const themeToggleButtonWidget = PF('themeToggleButtonWidget');
    if (themeToggleButtonWidget) {

        themeToggleButtonWidget.jq.find('.ui-icon').removeClass('pi pi-sun pi pi-moon');

        themeToggleButtonWidget.jq.find('.ui-icon').addClass(isDarkMode ? 'pi pi-moon' : 'pi pi-sun');
    }
}


document.addEventListener("DOMContentLoaded", function () {
    updateThemeButtonIcon();
});
