import {useState, useEffect} from 'react';

const SpotifyAuth = () => {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        console.log('Component mounted');
        // Récupérer le token depuis l'URL si présent
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');
        console.log('Token:', token);

        if (token) {
            console.log('Token found in URL');
            localStorage.setItem('jwt_token', token);
            // Nettoyer l'URL
            window.history.replaceState({}, document.title, window.location.pathname);
            fetchUserInfo(token);
        } else {
            // Si aucun token dans l'URL, vérifier le localStorage
            const storedToken = localStorage.getItem('jwt_token');
            if (storedToken) {
                fetchUserInfo(storedToken);
            }
        }
    }, []);

    const fetchUserInfo = async (token) => {
        const authToken = token || localStorage.getItem('jwt_token');
        if (!authToken) {
            setError('Non authentifié. Veuillez vous connecter.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8081/api/v1/user', {
                headers: {
                    "Authorization": `Bearer ${authToken}`,
                    "Content-Type": "application/json",
                }
            });

            if (response.ok) {
                const data = await response.json();
                console.log('User data:', data);
                setUser(data);
            } else {
                setError('Non authentifié. Veuillez vous connecter.');
                localStorage.removeItem('jwt_token');
            }
        } catch (err) {
            setError('Erreur lors de la récupération des données utilisateur.');
            console.error('Fetch error:', err);
        }
    };

    const handleLogin = () => {
        window.location.href = "http://localhost:8081/oauth2/authorization/spotify";
    };

    const handleLogout = () => {
        localStorage.removeItem('jwt_token');
        setUser(null);

        // api call
        fetch('http://localhost:8081/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
        }).then(r => {
            if (r.ok) {
                console.log('Logged out');
            } else {
                console.error('Logout failed');
            }
        });

    };

    if (error) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-gray-100">
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                    {error}
                    <button
                        onClick={() => setError(null)}
                        className="ml-4 underline"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    if (user) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-gray-100">
                <div className="bg-white text-black p-8 rounded-lg shadow-md">
                    <h1 className="text-2xl font-bold mb-4">Bienvenue !</h1>
                    <p><span className="font-semibold">Nom:</span> {user.name}</p>
                    <p><span className="font-semibold">Email:</span> {user.email}</p>
                    <p><img src={user.imgUrl} alt="image"/></p>
                    <button
                        onClick={handleLogout}
                        className="mt-4 bg-red-500 text-white px-6 py-2 rounded-full hover:bg-red-600 transition-colors"
                    >
                        Déconnexion
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md text-center">
                <h1 className="text-2xl font-bold mb-6">Connexion</h1>
                <button
                    onClick={handleLogin}
                    className="bg-green-500 text-white px-6 py-2 rounded-full hover:bg-green-600 transition-colors"
                >
                    Se connecter avec Spotify
                </button>
            </div>
        </div>
    );
};

export default SpotifyAuth;