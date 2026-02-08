let allCourses = [];

async function fetchCourses() {
    try {
        const res = await fetch('/existing-courses/all');
       allCourses = await res.json();
        displayCourses(allCourses);
    } catch (err) {
        console.error('Error loading courses:', err);
        document.getElementById('courseList').innerHTML = '<p>Error loading courses</p>';
    }
}

function displayCourses(courses) {
    const courseList = document.getElementById('courseList');
    courseList.innerHTML = '';
    courses.forEach(course => {
        const card = document.createElement('div');
        card.className = 'course-card';
        card.textContent = `${course.name}`;
        card.onclick = () => openEditSection(course);
        courseList.appendChild(card);
    });
}

function filterCourses() {
    const search = document.getElementById('searchBox').value.toLowerCase();
    const filtered = allCourses.filter(c =>
        c.name.toLowerCase().includes(search) ||
        (c.description && c.description.toLowerCase().includes(search))
    );
    displayCourses(filtered);
}

async function openEditSection(course) {
    const edit = document.getElementById('editSection');
    edit.innerHTML = `
        <h2>Edit Course: ${course.name}</h2>
        <label>Name: <input type="text" id="editName" value="${course.name}"></label><br><br>
        <label>Description: <textarea id="editDescription">${course.description || ''}</textarea></label><br><br>
        <button onclick="saveCourse(${course.id})">Save</button>
        <button onclick="deleteCourse(${course.id})">Delete</button>
        
        <button onclick="toggleEntities(${course.id}, this)">+ Classes/Subjects</button>
        <button onclick="addClass(${course.id})">+ Add Class</button>
        <div id="entities-${course.id}" class="entity-item"></div>
    `;
}

async function toggleEntities(courseId, btn) {
    const container = document.getElementById(`entities-${courseId}`);
    if (container.innerHTML !== '') {
        container.innerHTML = '';
        btn.textContent = '+ Classes/Subjects';
        return;
    }

    try {
        const res = await fetch(`/existing-courses/${courseId}`);
        const course = await res.json();
        btn.textContent = '^ Collapse';
        renderEntities(container, course);
    } catch (err) {
        console.error('Error fetching entities:', err);
        container.innerHTML = '<p>Error loading entities</p>';
    }
}

function renderEntities(container, course) {
    if (course.classes && course.classes.length > 0) {
        course.classes.forEach(cls => {
            const div = document.createElement('div');
            div.className = 'entity-item';
            div.innerHTML = `
                Class: ${cls.name}
                <span class="entity-actions">
                    <button onclick="deleteEntity('class', ${cls.id})">Delete</button>
                    <button onclick="toggleSubEntities('class', ${cls.id}, this)">+ Subjects</button>
                    <button onclick="addSubject(${cls.id})">+ Add Subject</button>
                </span>
                <div id="subjects-${cls.id}"></div>
            `;
            container.appendChild(div);
        });
    } else if (course.subjects && course.subjects.length > 0) {
        course.subjects.forEach(sub => {
            const div = document.createElement('div');
            div.className = 'entity-item';
            div.innerHTML = `
                Subject: ${sub.name}
                <span class="entity-actions">
                    <button onclick="deleteEntity('subject', ${sub.id})">Delete</button>
                    <button onclick="toggleSubEntities('subject', ${sub.id}, this)">+ Chapters</button>
                    <button onclick="addChapter(${sub.id})">+ Add Chapter</button>
                </span>
                <div id="chapters-${sub.id}"></div>
            `;
            container.appendChild(div);
        });
    } else {
        container.innerHTML = '<p>No classes or subjects found.</p>';
    }
}

async function toggleSubEntities(type, id, btn) {
    const containerId = type === 'class' ? `subjects-${id}` :
                        type === 'subject' ? `chapters-${id}` :
                        `notes-${id}`;
    const container = document.getElementById(containerId);

    if (container.innerHTML !== '') {
        container.innerHTML = '';
        btn.textContent = type === 'class' ? '+ Subjects' :
                          type === 'subject' ? '+ Chapters' :
                          '+ Notes';
        return;
    }

    let url;
    if (type === 'class') url = `/existing-courses/classes/${id}/subjects`;
    else if (type === 'subject') url = `/existing-courses/subjects/${id}/chapters`;
    else url = `/existing-courses/chapters/${id}/notes`;

    try {
        const res = await fetch(url);
        const items = await res.json();
        btn.textContent = '^ Collapse';

        if (type === 'class') {
            items.forEach(sub => {
                const div = document.createElement('div');
                div.className = 'entity-item';
                div.innerHTML = `
                    Subject: ${sub.name}
                    <span class="entity-actions">
                        <button onclick="deleteEntity('subject', ${sub.id})">Delete</button>
                        <button onclick="toggleSubEntities('subject', ${sub.id}, this)">+ Chapters</button>
                        <button onclick="addChapter(${sub.id})">+ Add Chapter</button>
                    </span>
                    <div id="chapters-${sub.id}"></div>
                `;
                container.appendChild(div);
            });
        } else if (type === 'subject') {
            items.forEach(ch => {
                const div = document.createElement('div');
                div.className = 'entity-item';
                div.innerHTML = `
                    Chapter: ${ch.name}
                    <span class="entity-actions">
                        <button onclick="deleteEntity('chapter', ${ch.id})">Delete</button>
                        <button onclick="toggleSubEntities('chapter', ${ch.id}, this)">+ Notes</button>
                        <button onclick="addNote(${ch.id})">+ Add Note</button>
                    </span>
                    <div id="notes-${ch.id}"></div>
                `;
                container.appendChild(div);
            });
        } else {
            items.forEach(note => {
                const div = document.createElement('div');
                div.className = 'entity-item';
                div.innerHTML = `
                    Note: ${note.title} (Price: ${note.price} ${note.isFree ? '- Free' : ''})
                    <span class="entity-actions">
                        <button onclick="deleteEntity('note', ${note.id})">Delete</button>
                        <button onclick="editNote(${note.id}, '${note.fileUrl}',${note.price}, ${note.isFree})">Edit</button>
                        <button onclick="viewNote('${note.fileUrl}')">View</button>
                    </span>
                `;
                container.appendChild(div);
            });
        }
    } catch (err) {
        console.error('Error fetching sub-entities:', err);
        container.innerHTML = '<p>Error loading items</p>';
    }
}

// ---- Add functions ----
async function addSubject(classId) {
    const name = prompt("Enter new subject name:");
    if (!name) return;
    const formData = new URLSearchParams();
    formData.append("classId", classId);
    formData.append("subjectName", name);

    await fetch('/admin/add-subject', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
    });
    fetchCourses();
}

async function addChapter(subjectId) {
    const name = prompt("Enter new chapter name:");
    if (!name) return;
    const formData = new URLSearchParams();
    formData.append("subjectId", subjectId);
    formData.append("chapterName", name);

    await fetch('/admin/add-chapter', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
    });
    fetchCourses();
}

async function addNote(chapterId) {
    const title = prompt("Enter note title:");
    if (!title) return;
    const fileUrl = prompt("Enter file URL (leave empty for now):") || '';
    const price = parseFloat(prompt("Enter price (0 for free):") || '0');
    const isFree = price === 0;

    const formData = new URLSearchParams();
    formData.append("chapterId", chapterId);
    formData.append("title", title);
    formData.append("fileUrl", fileUrl);
    formData.append("price", price);
    formData.append("isFree", isFree);

    await fetch('/admin/add-note', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
    });
    fetchCourses();
}

async function saveCourse(id) {
    const updatedCourse = {
        name: document.getElementById('editName').value,
        description: document.getElementById('editDescription').value
    };
    await fetch(`/existing-courses/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedCourse)
    });
    fetchCourses();
    document.getElementById('editSection').innerHTML = 'Select a course to edit';
}


async function deleteCourse(id) {
    if (confirm("Are you sure you want to delete this course?")) {
        await fetch(`/existing-courses/soft-delete-course/${id}`, { method: 'POST' });
        // remove from frontend immediately
        allCourses = allCourses.filter(c => c.id !== id);
        displayCourses(allCourses);
        document.getElementById('editSection').innerHTML = 'Select a course to edit';
    }
}    




function viewNote(fileUrl) {
    const fullUrl = `/uploads/${fileUrl}`;  // path where your PDFs are stored
    window.open(fullUrl, '_blank');
}





 async function editNote(id, fileUrl, price, isFree) {
    const newFileUrl = prompt("Enter new file URL:", fileUrl);
    if (newFileUrl === null) return;

    const newPrice = prompt("Enter new price (0 for free):", price);
    if (newPrice === null) return;

    const makeFree = confirm("Mark as free? Click OK for Yes, Cancel for No.");

    const updatedNote = {
        fileUrl: newFileUrl,
        price: parseFloat(newPrice),
        isFree: makeFree
    };

    try {
        await fetch(`/existing-courses/update-note/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedNote)
        });
        alert("Note updated successfully!");
        fetchCourses();
    } catch (err) {
        console.error("Error updating note:", err);
        alert("Failed to update note.");
    }
}   


async function addClass(courseId) {
    const className = prompt("Enter new class name:");
    if (className) {
        await fetch('/admin/add-class', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `courseId=${courseId}&className=${encodeURIComponent(className)}`
        });
        fetchCourses(); // Refresh list
    }
}





async function deleteEntity(type, id) {
    if (confirm(`Delete this ${type}?`)) {
        await fetch(`/existing-courses/soft-delete-${type}/${id}`, { method: 'POST' });
        // refresh list
        fetchCourses();
    }
}

fetchCourses();