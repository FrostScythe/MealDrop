import { ImageUp } from 'lucide-react';
import { useState } from 'react';
import axios from 'axios';

const AddFood = () => {

  const[image,setImage] = useState(false);
  const [data,setData] = useState({
    name:"",
    description:"",
    category:"",
    price:""
  });

  const onChangeHandler = (e) => {
    const name = e.target.name;
    const value = e.target.value;
    setData(data => ({...data,[name]:value}));
  }

  const onSubmitHandler = async (e) => {
    e.preventDefault();
    if(!image){
      alert("Please select an image to upload.");
      return;
    } 

    const formData = new FormData();
    formData.append('food', JSON.stringify(data));
    formData.append('file', image);

    try{
      const response = await axios.post('http://localhost:4000/api/food/add', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      if(response.status === 200){
        alert("Food uploaded successfully!");
        setData({
          name:"",
          description:"",
          category:"",
          price:""
        });
        setImage(false);
      }
    }catch(err){
      console.error("Error uploading food:", err);
      alert("Failed to upload food. Please try again.");
    }
  }

  return (
    <div className="d-flex align-items-center justify-content-center"
      style={{ height: 'calc(100vh - 72px)', background: '#f8f9fa' }}>

      <div className="card border-0 shadow-lg"
        style={{ width: '100%', maxWidth: '540px', borderRadius: '20px', overflow: 'hidden', margin: '0 1rem' }}>

        <div style={{ height: '5px', background: 'linear-gradient(90deg, #ff6b35, #f7c59f, #ff6b35)' }} />

        <div className="card-body px-4 px-md-5" style={{ paddingTop: '1rem', paddingBottom: '1rem' }}>

          {/* Header */}
          <div className="text-center mb-2">
            <span style={{ fontSize: '1.3rem' }}>🍽️</span>
            <h1 className="fw-bold mt-1 mb-0" style={{ color: '#1a1a1a', fontSize: '1.3rem' }}>
              Add New Recipe
            </h1>
            <p className="text-muted mb-0" style={{ fontSize: '0.75rem' }}>Share your delicious creation with the world</p>
          </div>

          <form onSubmit={onSubmitHandler}>

            {/* Name */}
            <div className="mb-2">
              <label htmlFor="name" className="form-label fw-semibold text-dark mb-1"
                style={{ fontSize: '0.7rem', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
                Recipe Name
              </label>
              <input type="text" className="form-control border-0 bg-light"
                id="name" required name="name" placeholder="e.g. Spicy Margherita Pizza"
                style={{ borderRadius: '10px', fontSize: '0.85rem', padding: '0.4rem 0.85rem' }}
                onChange={onChangeHandler} value={data.name}/> 
            </div>

            {/* Image Upload */}
            <div className="mb-2">
  <label className="form-label fw-semibold text-dark mb-1"
    style={{ fontSize: '0.7rem', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
    Cover Photo
  </label>
  <label htmlFor="image" className="d-flex align-items-center gap-2 w-100 bg-light"
    style={{ borderRadius: '10px', border: '2px dashed #ffa07a', cursor: 'pointer', padding: '0.45rem 0.9rem' }}
    onMouseEnter={e => e.currentTarget.style.background = '#fff0e6'}
    onMouseLeave={e => e.currentTarget.style.background = image ? 'transparent' : '#f8f9fa'}>

    {image ? (
      // ✅ Image preview when file is selected
      <img
        src={URL.createObjectURL(image)}
        alt="Cover preview"
        style={{ width: '100%', maxHeight: '160px', objectFit: 'cover', borderRadius: '8px' }}
      />
    ) : (
      // ⬆️ Upload UI when no image selected
      <>
        <div className="rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
          style={{ width: '30px', height: '30px', background: '#ff6b35', color: 'white' }}>
          <ImageUp size={14} />
        </div>
        <div>
          <div className="fw-semibold text-dark" style={{ fontSize: '0.8rem' }}>Click to upload image</div>
          <div className="text-muted" style={{ fontSize: '0.68rem' }}>PNG, JPG up to 5MB</div>
        </div>
      </>
    )}
  </label>
  <input type="file" id="image" hidden onChange={(e) => setImage(e.target.files[0])} />
</div>

            {/* Description */}
            <div className="mb-2">
              <label htmlFor="description" className="form-label fw-semibold text-dark mb-1"
                style={{ fontSize: '0.7rem', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
                Description
              </label>
              <textarea className="form-control border-0 bg-light"
                id="description" rows="2" required name="description"
                placeholder="Describe the flavors, ingredients, and story..."
                style={{ borderRadius: '10px', resize: 'none', fontSize: '0.85rem', padding: '0.4rem 0.85rem' }}
                onChange={onChangeHandler} value={data.description}>
              </textarea>
            </div>

            {/* Category + Price */}
            <div className="row g-2 mb-2">
              <div className="col-7">
                <label htmlFor="category" className="form-label fw-semibold text-dark mb-1"
                  style={{ fontSize: '0.7rem', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
                  Category
                </label>
                <select className="form-select border-0 bg-light" id="category" required name="category"
                  style={{ borderRadius: '10px', fontSize: '0.85rem', padding: '0.4rem 0.85rem' }}
                  onChange={onChangeHandler} value={data.category}>
                  <option value="">Select Category</option>
                  <option value="Cake">🎂 Cake</option>
                  <option value="Burger">🍔 Burger</option>
                  <option value="Pizza">🍕 Pizza</option>
                  <option value="Biryani">🍛 Biryani</option>
                  <option value="Salad">🥗 Salad</option>
                  <option value="Ice cream">🍦 Ice Cream</option>
                </select>
              </div>
              <div className="col-5">
                <label htmlFor="price" className="form-label fw-semibold text-dark mb-1"
                  style={{ fontSize: '0.7rem', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
                  Price ($)
                </label>
                <input type="number" className="form-control border-0 bg-light"
                  id="price" required name="price" placeholder="0.00"
                  style={{ borderRadius: '10px', fontSize: '0.85rem', padding: '0.4rem 0.85rem' }}
                  onChange={onChangeHandler} value={data.price}/>
              </div>
            </div>

            {/* Submit */}
            <div className="d-grid mt-3">
              <button type="submit" className="btn fw-bold text-white"
                style={{
                  background: 'linear-gradient(90deg, #ff6b35, #e84e0e)',
                  borderRadius: '10px', border: 'none',
                  padding: '0.6rem', fontSize: '0.9rem',
                  letterSpacing: '0.03em',
                  boxShadow: '0 4px 15px rgba(255,107,53,0.35)',
                }}
                onMouseEnter={e => { e.currentTarget.style.opacity = '0.9'; e.currentTarget.style.transform = 'translateY(-1px)'; }}
                onMouseLeave={e => { e.currentTarget.style.opacity = '1'; e.currentTarget.style.transform = 'translateY(0)'; }}>
                🍴 Publish Recipe
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  );
};

export default AddFood;