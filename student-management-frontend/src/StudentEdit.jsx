import { useState } from "react";

function StudentEdit({ student, onStudentUpdated, onCancel }) {

    const [name, setName] = useState(student.name);
    const [email, setEmail] = useState(student.email);
    const [age, setAge] = useState(student.age);

    function handleSubmit(event) {
        event.preventDefault();
        const updatedStudent = {
            id: student.id,
            name: name,
            email: email,
            age: Number(age)
        };

        onStudentUpdated(updatedStudent);
    }

    return (
        <div>
            <h2>Edit Student</h2>
            <form onSubmit={handleSubmit}>

                <div className="form-group">
                    <label>Name</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Age</label>
                    <input
                        type="number"
                        value={age}
                        onChange={(e) => setAge(e.target.value)}
                        required
                    />
                </div>

                <div className="btn-group">
                    <button type="submit" className="btn-primary">
                        Update
                    </button>
                    <button type="button" className="btn-secondary" onClick={onCancel}>
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
}

export default StudentEdit;