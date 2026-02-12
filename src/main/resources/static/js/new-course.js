/*function toggleClassSection(hasClasses) {
    document.getElementById('classes-section').style.display = hasClasses ? 'block' : 'none';
    document.getElementById('subjects-section').style.display = hasClasses ? 'none' : 'block';
}

function addClass() {
    const container = document.getElementById('classes-container');
    const classIndex = container.querySelectorAll('.class-block').length;

    const classDiv = document.createElement('div');
    classDiv.classList.add('class-block');
    classDiv.innerHTML = `
        <input type="text" name="classes[${classIndex}].name" placeholder="Class Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="subjects-container"></div>
        <button type="button" class="btn-tertiary" onclick="addSubject(this, ${classIndex})">+ Add Subject</button>
    `;
    container.appendChild(classDiv);
}

function addSubject(button, classIndex) {
    const container = button.previousElementSibling;
    const subjectIndex = container.querySelectorAll('.subject-block').length;

    const subjectDiv = document.createElement('div');
    subjectDiv.classList.add('subject-block');
    subjectDiv.innerHTML = `
        <input type="text" name="classes[${classIndex}].subjects[${subjectIndex}].name" placeholder="Subject Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="chapters-container"></div>
        <button type="button" class="btn-tertiary" onclick="addChapter(this, ${classIndex}, ${subjectIndex})">+ Add Chapter</button>
    `;
    container.appendChild(subjectDiv);
}

function addChapter(button, classIndex, subjectIndex) {
    const container = button.previousElementSibling;
    const chapterIndex = container.querySelectorAll('.chapter-block').length;

    const chapterDiv = document.createElement('div');
    chapterDiv.classList.add('chapter-block');
    chapterDiv.innerHTML = `
        <input type="text" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].name" placeholder="Chapter Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="notes-container"></div>
        <button type="button" class="btn-tertiary" onclick="addNote(this, ${classIndex}, ${subjectIndex}, ${chapterIndex})">+ Add Note</button>
    `;
    container.appendChild(chapterDiv);
}

function addNote(button, classIndex, subjectIndex, chapterIndex) {
    const container = button.previousElementSibling;
    const noteIndex = container.querySelectorAll('.note-block').length;

    const noteDiv = document.createElement('div');
    noteDiv.classList.add('note-block');
    noteDiv.innerHTML = `
        <input type="text" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].title" placeholder="Note Title" required>
        <input type="text" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].fileUrl" placeholder="Note URL" required>
        <input type="number" step="0.01" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].price" placeholder="Price (optional)">
        <label><input type="checkbox" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].isFree"> Free</label>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
    `;
    container.appendChild(noteDiv);
}

// For "No Classes" option
function addSubjectNoClass() {
    const container = document.getElementById('subjects-no-class-container');
    const subjectIndex = container.querySelectorAll('.subject-block').length;

    const subjectDiv = document.createElement('div');
    subjectDiv.classList.add('subject-block');
    subjectDiv.innerHTML = `
        <input type="text" name="subjects[${subjectIndex}].name" placeholder="Subject Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="chapters-container"></div>
        <button type="button" class="btn-tertiary" onclick="addChapterNoClass(this, ${subjectIndex})">+ Add Chapter</button>
    `;
    container.appendChild(subjectDiv);
}

function addChapterNoClass(button, subjectIndex) {
    const container = button.previousElementSibling;
    const chapterIndex = container.querySelectorAll('.chapter-block').length;

    const chapterDiv = document.createElement('div');
    chapterDiv.classList.add('chapter-block');
    chapterDiv.innerHTML = `
        <input type="text" name="subjects[${subjectIndex}].chapters[${chapterIndex}].name" placeholder="Chapter Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="notes-container"></div>
        <button type="button" class="btn-tertiary" onclick="addNoteNoClass(this, ${subjectIndex}, ${chapterIndex})">+ Add Note</button>
    `;
    container.appendChild(chapterDiv);
}

function addNoteNoClass(button, subjectIndex, chapterIndex) {
    const container = button.previousElementSibling;
    const noteIndex = container.querySelectorAll('.note-block').length;

    const noteDiv = document.createElement('div');
    noteDiv.classList.add('note-block');
    noteDiv.innerHTML = `
        <input type="text" name="subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].title" placeholder="Note Title" required>
        <input type="text" name="subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].fileUrl" placeholder="Note URL" required>
        <input type="number" step="0.01" name="subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].price" placeholder="Price (optional)">
        <label><input type="checkbox" name="subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].isFree"> Free</label>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
    `;
    container.appendChild(noteDiv);
}

// Re-indexing after delete
function renumberAll() {
    // Classes
    document.querySelectorAll('#classes-container .class-block').forEach((classBlock, classIndex) => {
        classBlock.querySelector('input[type="text"]').name = `classes[${classIndex}].name`;

        classBlock.querySelectorAll('.subjects-container .subject-block').forEach((subjectBlock, subjIndex) => {
            subjectBlock.querySelector('input[type="text"]').name = `classes[${classIndex}].subjects[${subjIndex}].name`;

            subjectBlock.querySelectorAll('.chapters-container .chapter-block').forEach((chapterBlock, chapIndex) => {
                chapterBlock.querySelector('input[type="text"]').name = `classes[${classIndex}].subjects[${subjIndex}].chapters[${chapIndex}].name`;

                chapterBlock.querySelectorAll('.notes-container .note-block').forEach((noteBlock, noteIndex) => {
                    const inputs = noteBlock.querySelectorAll('input');
                    inputs[0].name = `classes[${classIndex}].subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].title`;
                    inputs[1].name = `classes[${classIndex}].subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].fileUrl`;
                    inputs[2].name = `classes[${classIndex}].subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].price`;
                    inputs[3].name = `classes[${classIndex}].subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].isFree`;
                });
            });
        });
    });

    // Subjects without classes
    document.querySelectorAll('#subjects-no-class-container .subject-block').forEach((subjectBlock, subjIndex) => {
        subjectBlock.querySelector('input[type="text"]').name = `subjects[${subjIndex}].name`;

        subjectBlock.querySelectorAll('.chapters-container .chapter-block').forEach((chapterBlock, chapIndex) => {
            chapterBlock.querySelector('input[type="text"]').name = `subjects[${subjIndex}].chapters[${chapIndex}].name`;

            chapterBlock.querySelectorAll('.notes-container .note-block').forEach((noteBlock, noteIndex) => {
                const inputs = noteBlock.querySelectorAll('input');
                inputs[0].name = `subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].title`;
                inputs[1].name = `subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].fileUrl`;
                inputs[2].name = `subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].price`;
                inputs[3].name = `subjects[${subjIndex}].chapters[${chapIndex}].notes[${noteIndex}].isFree`;
            });
        });
    });
}

// Confirmation modal
function openConfirmation() {
    const details = gatherFormDetails();
    document.getElementById('confirmationDetails').innerHTML = details;
    document.getElementById('confirmationModal').style.display = 'block';
}

function closeConfirmation() {
    document.getElementById('confirmationModal').style.display = 'none';
}

function gatherFormDetails() {
    const name = document.getElementById('courseName').value;
    const description = document.getElementById('description').value;
    const hasClasses = document.querySelector('input[name="hasClasses"]:checked').value === 'yes';

    let details = `<h3>Course Name:</h3> ${name}<br>
                   <h3>Description:</h3> ${description}<br>
                   <h3>Has Classes:</h3> ${hasClasses ? 'Yes' : 'No'}<br><br>`;

    if (hasClasses) {
        const classes = document.querySelectorAll('#classes-container .class-block');
        if (classes.length === 0) {
            details += `<em>No classes added yet.</em>`;
        } else {
            classes.forEach((classBlock, classIndex) => {
                const className = classBlock.querySelector('input[type="text"]').value;
                details += `<strong>Class ${classIndex + 1}:</strong> ${className}<br>`;

                const subjects = classBlock.querySelectorAll('.subjects-container .subject-block');
                subjects.forEach((subjectBlock, subjIndex) => {
                    const subjectName = subjectBlock.querySelector('input[type="text"]').value;
                    details += `&nbsp;&nbsp;Subject ${subjIndex + 1}: ${subjectName}<br>`;

                    const chapters = subjectBlock.querySelectorAll('.chapters-container .chapter-block');
                    chapters.forEach((chapterBlock, chapIndex) => {
                        const chapterName = chapterBlock.querySelector('input[type="text"]').value;
                        details += `&nbsp;&nbsp;&nbsp;&nbsp;Chapter ${chapIndex + 1}: ${chapterName}<br>`;

                        const notes = chapterBlock.querySelectorAll('.notes-container .note-block');
                        notes.forEach((noteBlock, noteIndex) => {
                            const inputs = noteBlock.querySelectorAll('input');
                            const noteTitle = inputs[0].value;
                            const price = inputs[2].value;
                            const isFree = inputs[3].checked;
                            details += `&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Note ${noteIndex + 1}: ${noteTitle}`;
                            details += isFree ? ` (Free)<br>` : (price ? ` (Price: ₹${price})<br>` : ` (Price not set)<br>`);
                        });
                    });
                });
                details += `<br>`;
            });
        }
    } else {
        const subjects = document.querySelectorAll('#subjects-no-class-container .subject-block');
        if (subjects.length === 0) {
            details += `<em>No subjects added yet.</em>`;
        } else {
            subjects.forEach((subjectBlock, subjIndex) => {
                const subjectName = subjectBlock.querySelector('input[type="text"]').value;
                details += `<strong>Subject ${subjIndex + 1}:</strong> ${subjectName}<br>`;

                const chapters = subjectBlock.querySelectorAll('.chapters-container .chapter-block');
                chapters.forEach((chapterBlock, chapIndex) => {
                    const chapterName = chapterBlock.querySelector('input[type="text"]').value;
                    details += `&nbsp;&nbsp;Chapter ${chapIndex + 1}: ${chapterName}<br>`;

                    const notes = chapterBlock.querySelectorAll('.notes-container .note-block');
                    notes.forEach((noteBlock, noteIndex) => {
                        const inputs = noteBlock.querySelectorAll('input');
                        const noteTitle = inputs[0].value;
                        const price = inputs[2].value;
                        const isFree = inputs[3].checked;
                        details += `&nbsp;&nbsp;&nbsp;&nbsp;Note ${noteIndex + 1}: ${noteTitle}`;
                        details += isFree ? ` (Free)<br>` : (price ? ` (Price: ₹${price})<br>` : ` (Price not set)<br>`);
                    });
                });
                details += `<br>`;
            });
        }
    }

    return details;
}

function submitForm() {
    document.getElementById('courseForm').submit();
}*/

function toggleClassSection(hasClasses) {
    document.getElementById('classes-section').style.display = hasClasses ? 'block' : 'none';
    document.getElementById('subjects-section').style.display = hasClasses ? 'none' : 'block';
}

function addClass() {
    const container = document.getElementById('classes-container');
    const classIndex = container.querySelectorAll('.class-block').length;

    const classDiv = document.createElement('div');
    classDiv.classList.add('class-block');
    classDiv.innerHTML = `
        <div class="header-row">
            <input type="text" name="classes[${classIndex}].name" placeholder="Class Name" required>
            <button type="button" class="btn-delete" onclick="this.parentElement.parentElement.remove(); renumberAll()">Delete Class</button>
        </div>
        <div class="subjects-container"></div>
        <button type="button" class="btn-tertiary" onclick="addSubject(this, ${classIndex})">+ Add Subject</button>
    `;
    container.appendChild(classDiv);
}

function addSubject(button, classIndex) {
    const container = button.previousElementSibling;
    const subjectIndex = container.querySelectorAll('.subject-block').length;

    const subjectDiv = document.createElement('div');
    subjectDiv.classList.add('subject-block');
    subjectDiv.innerHTML = `
        <input type="text" name="classes[${classIndex}].subjects[${subjectIndex}].name" placeholder="Subject Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="chapters-container"></div>
        <button type="button" class="btn-tertiary" onclick="addChapter(this, ${classIndex}, ${subjectIndex})">+ Add Chapter</button>
    `;
    container.appendChild(subjectDiv);
}

function addChapter(button, classIndex, subjectIndex) {
    const container = button.previousElementSibling;
    const chapterIndex = container.querySelectorAll('.chapter-block').length;

    const chapterDiv = document.createElement('div');
    chapterDiv.classList.add('chapter-block');
    chapterDiv.innerHTML = `
        <input type="text" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].name" placeholder="Chapter Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="notes-container"></div>
        <button type="button" class="btn-tertiary" onclick="addNote(this, ${classIndex}, ${subjectIndex}, ${chapterIndex})">+ Add Note</button>
    `;
    container.appendChild(chapterDiv);
}

function addNote(button, classIndex, subjectIndex, chapterIndex) {
    const container = button.previousElementSibling;
    const noteIndex = container.querySelectorAll('.note-block').length;

    const noteDiv = document.createElement('div');
    noteDiv.classList.add('note-block');
    noteDiv.innerHTML = `
        <input type="text" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].title" placeholder="Note Title" required>
        <input type="file" name="noteFiles" accept=".pdf" required> 
        <input type="number" step="0.01" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].price" placeholder="Price">
        <label><input type="checkbox" name="classes[${classIndex}].subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].isFree"> Free</label>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
    `;
    container.appendChild(noteDiv);
}

// Functions for "No Classes" option
function addSubjectNoClass() {
    const container = document.getElementById('subjects-no-class-container');
    const subjectIndex = container.querySelectorAll('.subject-block').length;

    const subjectDiv = document.createElement('div');
    subjectDiv.classList.add('subject-block');
    subjectDiv.innerHTML = `
        <input type="text" name="subjects[${subjectIndex}].name" placeholder="Subject Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="chapters-container"></div>
        <button type="button" class="btn-tertiary" onclick="addChapterNoClass(this, ${subjectIndex})">+ Add Chapter</button>
    `;
    container.appendChild(subjectDiv);
}

function addChapterNoClass(button, subjectIndex) {
    const container = button.previousElementSibling;
    const chapterIndex = container.querySelectorAll('.chapter-block').length;

    const chapterDiv = document.createElement('div');
    chapterDiv.classList.add('chapter-block');
    chapterDiv.innerHTML = `
        <input type="text" name="subjects[${subjectIndex}].chapters[${chapterIndex}].name" placeholder="Chapter Name" required>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
        <div class="notes-container"></div>
        <button type="button" class="btn-tertiary" onclick="addNoteNoClass(this, ${subjectIndex}, ${chapterIndex})">+ Add Note</button>
    `;
    container.appendChild(chapterDiv);
}

function addNoteNoClass(button, subjectIndex, chapterIndex) {
    const container = button.previousElementSibling;
    const noteIndex = container.querySelectorAll('.note-block').length;

    const noteDiv = document.createElement('div');
    noteDiv.classList.add('note-block');
    noteDiv.innerHTML = `
        <input type="text" name="subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].title" placeholder="Note Title" required>
        <input type="file" name="noteFiles" accept=".pdf" required>
        <input type="number" step="0.01" name="subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].price" placeholder="Price">
        <label><input type="checkbox" name="subjects[${subjectIndex}].chapters[${chapterIndex}].notes[${noteIndex}].isFree"> Free</label>
        <button type="button" onclick="this.parentElement.remove(); renumberAll()">Delete</button>
    `;
    container.appendChild(noteDiv);
}

function renumberAll() {
    // Re-indexing for Classes hierarchy
    document.querySelectorAll('#classes-container .class-block').forEach((classBlock, classIdx) => {
        classBlock.querySelector('input[name*=".name"]').name = `classes[${classIdx}].name`;
        classBlock.querySelectorAll('.subject-block').forEach((subBlock, subIdx) => {
            subBlock.querySelector('input[name*=".name"]').name = `classes[${classIdx}].subjects[${subIdx}].name`;
            subBlock.querySelectorAll('.chapter-block').forEach((chapBlock, chapIdx) => {
                chapBlock.querySelector('input[name*=".name"]').name = `classes[${classIdx}].subjects[${subIdx}].chapters[${chapIdx}].name`;
                chapBlock.querySelectorAll('.note-block').forEach((noteBlock, noteIdx) => {
                    const inputs = noteBlock.querySelectorAll('input');
                    inputs[0].name = `classes[${classIdx}].subjects[${subIdx}].chapters[${chapIdx}].notes[${noteIdx}].title`;
                    // File inputs stay as 'noteFiles' for the List mapping
                    inputs[2].name = `classes[${classIdx}].subjects[${subIdx}].chapters[${chapIdx}].notes[${noteIdx}].price`;
                    inputs[3].name = `classes[${classIdx}].subjects[${subIdx}].chapters[${chapIdx}].notes[${noteIdx}].isFree`;
                });
            });
        });
    });

    // Re-indexing for No-Class hierarchy
    document.querySelectorAll('#subjects-no-class-container .subject-block').forEach((subBlock, subIdx) => {
        subBlock.querySelector('input[name*=".name"]').name = `subjects[${subIdx}].name`;
        subBlock.querySelectorAll('.chapter-block').forEach((chapBlock, chapIdx) => {
            chapBlock.querySelector('input[name*=".name"]').name = `subjects[${subIdx}].chapters[${chapIdx}].name`;
            chapBlock.querySelectorAll('.note-block').forEach((noteBlock, noteIdx) => {
                const inputs = noteBlock.querySelectorAll('input');
                inputs[0].name = `subjects[${subIdx}].chapters[${chapIdx}].notes[${noteIdx}].title`;
                inputs[2].name = `subjects[${subIdx}].chapters[${chapIdx}].notes[${noteIdx}].price`;
                inputs[3].name = `subjects[${subIdx}].chapters[${chapIdx}].notes[${noteIdx}].isFree`;
            });
        });
    });
}

function openConfirmation() {
    const details = gatherFormDetails();
    document.getElementById('confirmationDetails').innerHTML = details;
    document.getElementById('confirmationModal').style.display = 'block';
}

function closeConfirmation() {
    document.getElementById('confirmationModal').style.display = 'none';
}

function gatherFormDetails() {
    const name = document.getElementById('courseName').value;
    const hasClasses = document.querySelector('input[name="hasClasses"]:checked').value === 'yes';
    let details = `<h3>Course Name:</h3> ${name}<br><h3>Structure:</h3> ${hasClasses ? 'By Class' : 'Direct Subjects'}<br><br>`;
    
    // Simplification for preview: count total files
    const fileCount = document.querySelectorAll('input[type="file"]').length;
    details += `<strong>Total Files Selected:</strong> ${fileCount} PDFs<br>`;
    
    return details;
}

function submitForm() {
    const btn = document.querySelector('#confirmationModal .btn-primary');
    btn.innerHTML = "Uploading... Please Wait";
    btn.disabled = true; // Prevent double-clicks
    document.getElementById('courseForm').submit();
}