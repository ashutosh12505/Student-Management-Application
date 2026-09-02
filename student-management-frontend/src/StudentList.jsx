function StudentList({ students, onEdit, onDelete, isAdmin }) {
    return (
        <div>
            <h2>Students</h2>

            {students.length === 0 ? (
                <p>No students found.</p>
            ) : (
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Age</th>
                            {isAdmin && <th>Actions</th>}
                        </tr>
                    </thead>
                    <tbody>
                        {students.map(student => (
                            <tr key={student.id}>
                                <td>{student.id}</td>
                                <td>{student.name}</td>
                                <td>{student.email}</td>
                                <td>{student.age}</td>
                                {isAdmin && (
                                    <td>
                                        <div className="btn-group">
                                            <button
                                                className="btn-primary btn-sm"
                                                onClick={() => onEdit(student)}
                                            >
                                                Edit
                                            </button>
                                            <button
                                                className="btn-danger btn-sm"
                                                onClick={() => onDelete(student.id)}
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default StudentList;