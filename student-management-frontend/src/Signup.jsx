import { useState } from "react";

function Signup({ onSignup, onGoToLogin }) {

    const [showPassword, setShowPassword] = useState(false);

    function handleSubmit(event) {
        event.preventDefault();

        const name = event.target.name.value;
        const email = event.target.email.value;
        const password = event.target.password.value;

        if (name && email && password) {
            onSignup();
        }
    }

    return (
        <div className="card card-narrow">
            <h1>Student Management System</h1>
            <h2 className="mt-16">Create Account</h2>

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>Name</label>
                    <input
                        type="text"
                        name="name"
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        name="email"
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
                    Create Account
                </button>

            </form>

            <p className="mt-16">
                Already have an account?
            </p>

            <button className="btn-link" onClick={onGoToLogin}>
                Login
            </button>
        </div>
    );
}

export default Signup;