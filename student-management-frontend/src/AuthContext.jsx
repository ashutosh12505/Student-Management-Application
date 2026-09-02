import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

function decodeToken(token) {
    try {
        const payload = token.split(".")[1];
        const decoded = JSON.parse(atob(payload));
        return decoded;
    } catch {
        return null;
    }
}

export function AuthProvider({ children }) {

    const [token, setToken] = useState(
        localStorage.getItem("token")
    );

    const decoded = token ? decodeToken(token) : null;
    const username = decoded ? decoded.sub : null;
    const role = decoded ? decoded.role : null;

    function login(jwtToken) {
        localStorage.setItem("token", jwtToken);
        setToken(jwtToken);
    }

    function logout() {
        localStorage.removeItem("token");
        setToken(null);
    }

    const isLoggedIn = !!token;

    return (
        <AuthContext.Provider
            value={{
                token,
                isLoggedIn,
                username,
                role,
                login,
                logout
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}