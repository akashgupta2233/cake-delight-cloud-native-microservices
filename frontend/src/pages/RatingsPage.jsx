import { useEffect, useState } from 'react';
import { getRatingsByCakeId, getAverageRating, createRating, getCakes } from '../services/api';

function RatingsPage() {
  const [cakeId, setCakeId] = useState('');
  const [cakes, setCakes] = useState([]);
  const [cakesLoading, setCakesLoading] = useState(false);
  const [cakesError, setCakesError] = useState('');

  const [ratings, setRatings] = useState([]);
  const [average, setAverage] = useState(null);
  const [ratingValue, setRatingValue] = useState('');
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const loadCakes = async () => {
      setCakesLoading(true);
      setCakesError('');
      try {
        const data = await getCakes();
        setCakes(data);
      } catch (err) {
        setCakesError('Failed to load cakes.');
        setCakes([]);
      } finally {
        setCakesLoading(false);
      }
    };

    loadCakes();
  }, []);

  const loadRatings = async () => {
    if (!cakeId) {
      setError('Please select a cake.');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const [ratingsData, averageData] = await Promise.all([
        getRatingsByCakeId(cakeId),
        getAverageRating(cakeId)
      ]);
      setRatings(ratingsData);
      setAverage(averageData.averageRating ?? null);
    } catch (err) {
      setError('Failed to load ratings.');
      setRatings([]);
      setAverage(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (!cakeId) {
      setError('Please select a cake.');
      return;
    }

    if (!ratingValue) {
      setError('Please enter a rating value.');
      return;
    }

    try {
      await createRating({
        cakeId: Number(cakeId),
        ratingValue: Number(ratingValue),
        review: comment
      });
      setSuccess('Rating submitted successfully.');
      setRatingValue('');
      setComment('');
      await loadRatings();
    } catch (err) {
      setError('Failed to submit rating.');
    }
  };

  useEffect(() => {
    if (cakeId) {
      setAverage(null);
      setRatings([]);
    }
  }, [cakeId]);

  // build lookup map for cake names
  const cakeMap = new Map(cakes.map((c) => [c.id, c.name]));

  return (
    <div>
      <h1>Ratings</h1>

      <div className="ratings-form">
        <label>
          Select Cake
          {cakesLoading ? (
            <div>Loading cakes...</div>
          ) : cakesError ? (
            <div className="status-message status-error">{cakesError}</div>
          ) : (
            <select value={cakeId} onChange={(e) => setCakeId(e.target.value)}>
              <option value="">-- Select a cake --</option>
              {cakes.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          )}
        </label>
        <button type="button" onClick={loadRatings} disabled={!cakeId}>
          Load Ratings
        </button>
      </div>

      {loading && <p>Loading...</p>}
      {error && <p className="status-message status-error">{error}</p>}
      {success && <p className="status-message status-success">{success}</p>}

      {average !== null && (
        <p className="rating-average">Average Rating: {average}</p>
      )}

      <div className="ratings-list">
        {ratings.length > 0 ? (
          <table className="rating-table">
            <thead>
              <tr>
                <th>Rating ID</th>
                <th>Cake</th>
                <th>Rating Value</th>
                <th>Comment</th>
              </tr>
            </thead>
            <tbody>
              {ratings.map((rating) => (
                <tr key={rating.id}>
                  <td>{rating.id}</td>
                  <td>{cakeMap.get(rating.cakeId) ?? 'Unknown Cake'}</td>
                  <td>{rating.ratingValue}</td>
                  <td>{rating.review || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          cakeId && !loading && <p>No ratings found for this cake.</p>
        )}
      </div>

      <form className="rating-form" onSubmit={handleSubmit}>
        <h2>Rate a Cake</h2>
        <label>
          Select Cake
          {cakesLoading ? (
            <div>Loading cakes...</div>
          ) : cakesError ? (
            <div className="status-message status-error">{cakesError}</div>
          ) : (
            <select value={cakeId} onChange={(e) => setCakeId(e.target.value)}>
              <option value="">-- Select a cake --</option>
              {cakes.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          )}
        </label>
        <label>
          Rating:
          <input
            type="number"
            min="1"
            max="5"
            value={ratingValue}
            onChange={(e) => setRatingValue(e.target.value)}
          />
        </label>
        <label>
          Comment
          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
          />
        </label>
        <button type="submit">Submit Rating</button>
      </form>
    </div>
  );
}

export default RatingsPage;
