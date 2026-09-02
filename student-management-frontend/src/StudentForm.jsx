import { useState } from "react";

function StudentForm({ onStudentAdded }) {

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [age, setAge] = useState("");

    function handleSubmit(event) {
        event.preventDefault();

        const student = {
            name: name,
            email: email,
            age: age
        };

        onStudentAdded(student);

        setName("");
        setEmail("");
        setAge("");
    }

    return (
        <div>
            <h2>Add Student</h2>
            <form onSubmit={handleSubmit}>

                <div className="form-group">
                    <label>Name</label>
                    <input type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Email</label>
                    <input type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Age</label>
                    <input type="number"
                        value={age}
                        onChange={(e) => setAge(e.target.value)}
                        required
                    />
                </div>

                <button type="submit" className="btn-success">
                    Add Student
                </button>

            </form>
        </div>
    );
}

export default StudentForm;