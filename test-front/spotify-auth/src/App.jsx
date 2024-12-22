import SpotifyAuth from "./components/SpotifyAuth.jsx";
import {BrowserRouter} from "react-router-dom";

function App() {
    return (
        <BrowserRouter>
            <SpotifyAuth/>
        </BrowserRouter>
    )
}

export default App