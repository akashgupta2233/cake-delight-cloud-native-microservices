import { useEffect, useState } from 'react';
import api from '../services/api';

function CatalogPage() {
  const [cakes, setCakes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadCakes = async () => {
      setLoading(true);
      setError(null);

      try {
        const response = await api.get('/api/catalog/cakes');
        setCakes(response.data);
      } catch (err) {
        setError('Unable to load cakes.');
      } finally {
        setLoading(false);
      }
    };

    loadCakes();
  }, []);

  const addToBasket = async (cake) => {
    try {
      await api.post('/api/orders/basket', {
        cakeId: cake.id,
        cakeName: cake.name,
        price: cake.price,
        quantity: 1
      });
      alert(`Added ${cake.name} to basket.`);
    } catch (err) {
      alert('Could not add cake to basket.');
    }
  };

  if (loading) {
    return <div>Loading cakes...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  return (
    <div>
      <h1>Catalog</h1>
      <div className="card-grid">
        {cakes.map((cake) => (
          <div key={cake.id} className="card">
            {cake.imageUrl && (
              <div style={{textAlign: 'center', marginBottom: 12}}>
                <img src={cake.imageUrl} alt={cake.name} style={{maxWidth: '100%', height: 160, objectFit: 'cover', borderRadius: 8}} />
              </div>
            )}
            <h2>{cake.name}</h2>
            <p>{cake.description}</p>
            <p>
              <strong>Price:</strong> {cake.price}
            </p>
            <button type="button" onClick={() => addToBasket(cake)}>
              Add To Basket
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CatalogPage;
