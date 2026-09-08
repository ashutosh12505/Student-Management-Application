import { useState, useEffect } from "react";
import { useAuth } from "./AuthContext";
import Login from "./Login";
import Signup from "./Signup";
import Dashboard from "./Dashboard";

// const API_URL = "http://localhost:8080";
const API_URL = import.meta.env.VITE_API_URL;

function App() {

    const { token, isLoggedIn, username, role, logout } = useAuth();

    const [screen, setScreen] = useState("login");

    const [students, setStudents] = useState([]);

    const [editingStudent, setEditingStudent] = useState(null);

    const [searchedStudent, setSearchedStudent] = useState(null);

    const [errorMessage, setErrorMessage] = useState("");

    const isAdmin = role === "ADMIN";


    useEffect(() => {
        if (isLoggedIn) {
            setScreen("dashboard");
            fetchStudents();
        } else {
            setScreen("login");
        }
    }, [isLoggedIn]);


    async function fetchStudents() {
        try {
            const response = await fetch(API_URL + "/students", {
                headers: {
                    "Authorization": "Bearer " + token
                }
            });

            if (!response.ok) {
                throw new Error("Failed to fetch students");
            }

            const data = await response.json();
            setStudents(data);
        } catch {
            setErrorMessage("Failed to load students.");
        }
    }


    async function addStudent(student) {
        setErrorMessage("");
        try {
            const response = await fetch(API_URL + "/students", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    name: student.name,
                    email: student.email,
                    age: Number(student.age)
                })
            });

            if (!response.ok) {
                if (response.status === 403) {
                    setErrorMessage("You don't have permission to add students.");
                } else {
                    const errorData = await response.json();
                    setErrorMessage(errorData.message || "Failed to add student.");
                }
                return;
            }

            const savedStudent = await response.json();
            setStudents((prev) => [...prev, savedStudent]);
        } catch {
            setErrorMessage("Failed to add student.");
        }
    }


    function editStudent(student) {
        setEditingStudent(student);
    }


    async function updateStudent(updatedStudent) {
        setErrorMessage("");
        try {
            const response = await fetch(
                API_URL + "/students/" + updatedStudent.id,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify({
                        name: updatedStudent.name,
                        email: updatedStudent.email,
                        age: Number(updatedStudent.age)
                    })
                }
            );

            if (!response.ok) {
                if (response.status === 403) {
                    setErrorMessage("You don't have permission to update students.");
                } else {
                    const errorData = await response.json();
                    setErrorMessage(errorData.message || "Failed to update student.");
                }
                return;
            }

            const saved = await response.json();
            setStudents((prev) =>
                prev.map((s) => (s.id === saved.id ? saved : s))
            );
            setEditingStudent(null);
        } catch {
            setErrorMessage("Failed to update student.");
        }
    }


    async function deleteStudent(id) {
        setErrorMessage("");
        try {
            const response = await fetch(
                API_URL + "/students/" + id,
                {
                    method: "DELETE",
                    headers: {
                        "Authorization": "Bearer " + token
                    }
                }
            );

            if (!response.ok) {
                if (response.status === 403) {
                    setErrorMessage("You don't have permission to delete students.");
                } else {
                    setErrorMessage("Failed to delete student.");
                }
                return;
            }

            setStudents((prev) =>
                prev.filter((s) => s.id !== id)
            );
        } catch {
            setErrorMessage("Failed to delete student.");
        }
    }


    async function searchStudent(searchId) {
        setErrorMessage("");

        try {
            const response = await fetch(
                API_URL + "/students/" + searchId,
                {
                    headers: {
                        "Authorization": "Bearer " + token
                    }
                }
            );

            if (!response.ok) {
                setSearchedStudent(null);
                setErrorMessage("Student with ID " + searchId + " not found.");
                return;
            }

            const data = await response.json();
            setSearchedStudent(data);
        } catch {
            setSearchedStudent(null);
            setErrorMessage("Failed to search student.");
        }
    }


    function handleLogout() {
        logout();
        setEditingStudent(null);
        setSearchedStudent(null);
        setStudents([]);
        setErrorMessage("");
    }


    if (screen === "login") {
        return (
            <Login
                onGoToSignup={() => setScreen("signup")}
            />
        );
    }


    if (screen === "signup") {
        return (
            <Signup
                onSignup={() => setScreen("login")}
                onGoToLogin={() => setScreen("login")}
            />
        );
    }


    if (screen === "dashboard") {
        return (
            <Dashboard
                students={students}
                onStudentAdded={addStudent}
                editingStudent={editingStudent}
                onStudentUpdated={updateStudent}
                onCancelEdit={() => setEditingStudent(null)}
                onEdit={editStudent}
                onDelete={deleteStudent}
                onSearch={searchStudent}
                searchedStudent={searchedStudent}
                onClearSearch={() => setSearchedStudent(null)}
                errorMessage={errorMessage}
                username={username}
                isAdmin={isAdmin}
                onLogout={handleLogout}
            />
        );
    }
}

export default App;