import { useState } from "react";
import { useAuth } from "./AuthContext";

function Login({ onGoToSignup }) {

    const { login } = useAuth();

    const [showPassword, setShowPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    async function handleSubmit(event) {
        event.preventDefault();
        setErrorMessage("");

        const username = event.target.username.value;
        const password = event.target.password.value;

        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/auth/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            if (!response.ok) {
                setErrorMessage("Invalid username or password. Please try again.");
                return;
            }

            const jwtToken = await response.text();
            login(jwtToken);

        } catch {
            setErrorMessage("Unable to connect to server. Please try again later.");
        }
    }

    return (
        <div className="card card-narrow">
            <h1>Student Management System</h1>

            <h2 className="mt-16">Login</h2>

            {errorMessage && (
                <div className="error-message">
                    {errorMessage}
                </div>
            )}

            <form onSubmit={handleSubmit}>

                <div className="form-group">
                    <label>Username</label>
                    <input
                        type="text"
                        name="username"
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <div className="password-wrapper">
                        <input
                            type={showPassword ? "text" : "password"}
                            name="password"
                            required
                        />
                        <span
                            className="password-toggle"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            {showPassword ? "🙈" : "👁"}
                        </span>
                    </div>
                </div>

                <button type="submit" className="btn-primary">
                    Login
                </button>

            </form>

            <p className="mt-16">
                Don't have an account?
            </p>

            <button className="btn-link" onClick={onGoToSignup}>
                Sign Up
            </button>
        </div>
    );
}

export default Login;