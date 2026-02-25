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
    
    
    const fileCount = document.querySelectorAll('input[type="file"]').length;
    details += `<strong>Total Files Selected:</strong> ${fileCount} PDFs<br>`;
    
    return details;
}

function submitForm() {
    const btn = document.querySelector('#confirmationModal .btn-primary');
    btn.innerHTML = "Uploading... Please Wait";
    btn.disabled = true; 
    document.getElementById('courseForm').submit();
}