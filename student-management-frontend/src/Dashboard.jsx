import { useState } from "react";
import Modal from "./Modal";
import StudentForm from "./StudentForm";
import StudentList from "./StudentList";
import StudentEdit from "./StudentEdit";

function Dashboard({
    students,
    onStudentAdded,
    editingStudent,
    onStudentUpdated,
    onCancelEdit,
    onEdit,
    onDelete,
    onSearch,
    searchedStudent,
    onClearSearch,
    errorMessage,
    username,
    isAdmin,
    onLogout
}) {

    const [showAddModal, setShowAddModal] = useState(false);
    const [showSearchModal, setShowSearchModal] = useState(false);
    const [searchId, setSearchId] = useState("");

    function handleAddStudent(student) {
        onStudentAdded(student);
        setShowAddModal(false);
    }

    function handleSearch() {
        if (!searchId) return;
        onSearch(searchId);
    }

    function handleCloseSearch() {
        setShowSearchModal(false);
        setSearchId("");
        onClearSearch();
    }

    return (
        <div className="card">

            <h1 className="main-heading">Student Management System</h1>

            {errorMessage && (
                <div className="error-message">
                    {errorMessage}
                </div>
            )}

            <div className="toolbar">
                <div className="toolbar-left">
                    {isAdmin && (
                        <button
                            className="btn-primary"
                            onClick={() => setShowAddModal(true)}
                        >
                            + Add Student
                        </button>
                    )}

                    <button
                        className="btn-secondary"
                        onClick={() => setShowSearchModal(true)}
                    >
                        🔍 Search
                    </button>
                </div>

                <div className="toolbar-right">
                    <span className="username-badge">{username}</span>
                    <button className="btn-logout" onClick={onLogout}>
                        Logout
                    </button>
                </div>
            </div>

            {/* Add Student Modal */}
            {showAddModal && (
                <Modal onClose={() => setShowAddModal(false)}>
                    <StudentForm onStudentAdded={handleAddStudent} />
                </Modal>
            )}

            {/* Search Modal */}
            {showSearchModal && (
                <Modal onClose={handleCloseSearch}>
                    <h2>Search Student</h2>

                    <form onSubmit={(e) => { e.preventDefault(); handleSearch(); }}>
                        <div className="form-group">
                            <label>Student ID</label>
                            <input
                                type="number"
                                placeholder="Enter Student ID"
                                value={searchId}
                                onChange={(e) => setSearchId(e.target.value)}
                                autoFocus
                            />
                        </div>

                        <button type="submit" className="btn-primary">
                            Search
                        </button>
                    </form>

                    {searchedStudent && (
                        <div className="search-result-box">
                            <h3>Result</h3>
                            <dl className="search-result">
                                <dt>ID</dt>
                                <dd>{searchedStudent.id}</dd>
                                <dt>Name</dt>
                                <dd>{searchedStudent.name}</dd>
                                <dt>Email</dt>
                                <dd>{searchedStudent.email}</dd>
                                <dt>Age</dt>
                                <dd>{searchedStudent.age}</dd>
                            </dl>
                        </div>
                    )}
                </Modal>
            )}

            {/* Edit Student Modal */}
            {editingStudent && (
                <Modal onClose={onCancelEdit}>
                    <StudentEdit
                        student={editingStudent}
                        onStudentUpdated={onStudentUpdated}
                        onCancel={onCancelEdit}
                    />
                </Modal>
            )}

            <div className="section">
                <StudentList
                    students={students}
                    onEdit={isAdmin ? onEdit : null}
                    onDelete={isAdmin ? onDelete : null}
                    isAdmin={isAdmin}
                />
            </div>

        </div>
    );
}

export default Dashboard;