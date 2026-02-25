console.log("bin.js loaded");

async function loadDeleted(type, element) {
    
    document.querySelectorAll('.sidebar li').forEach(li => li.classList.remove('active'));
    if (element) element.classList.add('active');

    const title = `Deleted ${type.charAt(0).toUpperCase() + type.slice(1)}`;
    document.getElementById('bin-title').innerText = title;

    const container = document.getElementById('deleted-list');
    container.innerHTML = 'Loading...';

    try {
        const res = await fetch(`/bin/${type}`);
        const items = await res.json();

        if (!items || items.length === 0) {
            container.textContent = '';
            const p = document.createElement('p');
            p.textContent = 'No private files found';
            container.appendChild(p);
            return;
        }

        container.innerHTML = '';
        items.forEach(item => {
            const div = document.createElement('div');
            div.className = 'deleted-item';
            div.innerHTML = `
                <p><strong>${item.name || item.title}</strong></p>
                <p>Path: ${item.fullPath}</p>
                <button onclick="restoreItem('${type}', ${item.id})">Restore</button>
                <button onclick="permanentlyDelete('${type}', ${item.id})">Delete Permanently</button>`;
                if (type==='notes'){
                const editBtn = document.createElement('button');
editBtn.innerText = "Edit & Re-upload";
editBtn.addEventListener('click', () => editDeletedNote(item.id, item.fileUrl, item.price, item.isFree));
div.appendChild(editBtn);
                }
            
            container.appendChild(div);
        });
    } catch (err) {
        console.error('Error loading deleted items:', err);
        container.innerHTML = '<p>Error loading deleted items</p>';
    }
}

async function restoreItem(type, id) {
    if (confirm(`Restore this ${type}?`)) {
        await fetch(`/bin/restore/${type}/${id}`, { method: 'PUT' });
        loadDeleted(type, document.querySelector('.sidebar li.active'));
    }
}

async function permanentlyDelete(type, id) {
    if (confirm(`Permanently delete this ${type}? This cannot be undone.`)) {
        await fetch(`/bin/permanent/${type}/${id}`, { method: 'DELETE' });
        

        loadDeleted(type, document.querySelector('.sidebar li.active'));
    }
}

async function editDeletedNote(id, fileUrl, price, isFree) {
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

    await fetch(`/existing-courses/update-note/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedNote)
    });

    alert("Note updated successfully!");
    loadDeleted('notes', document.querySelector('.sidebar li:nth-child(5)'));
}

// Auto-load courses on page load
window.onload = () => {
    const firstItem = document.querySelector('.sidebar li');
    if (firstItem) firstItem.click();
};