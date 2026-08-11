import { BrowserRouter, Route, Routes, NavLink } from 'react-router-dom';
import './App.css';
import CatalogPage from './pages/CatalogPage';
import BasketPage from './pages/BasketPage';
import RatingsPage from './pages/RatingsPage';
import NotificationsPage from './pages/NotificationsPage';

function App() {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <header className="navbar">
          <h1>Cake Delight</h1>
          <nav>
            <NavLink to="/" end>
              Catalog
            </NavLink>
            <NavLink to="/basket">Basket</NavLink>
            <NavLink to="/ratings">Ratings</NavLink>
            <NavLink to="/notifications">Notifications</NavLink>
          </nav>
        </header>

        <main className="page-content">
          <Routes>
            <Route path="/" element={<CatalogPage />} />
            <Route path="/basket" element={<BasketPage />} />
            <Route path="/ratings" element={<RatingsPage />} />
            <Route path="/notifications" element={<NotificationsPage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App

