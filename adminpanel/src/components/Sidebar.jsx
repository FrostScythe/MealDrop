import { Link } from 'react-router-dom';
import { CirclePlus, ShoppingCart,List } from 'lucide-react';
import { logo } from '../assets/assets';

const Sidebar = ({ sidebarVisible }) => {
  return (
     <div className={`border-end bg-white ${sidebarVisible ? '' : 'd-none'}`} id="sidebar-wrapper">
                <div className="sidebar-heading border-bottom bg-light">
                    <img src={logo} alt="Logo" height={48} width={50} />
                </div>
                <div className="list-group list-group-flush">
                    <Link className="list-group-item list-group-item-action list-group-item-light p-3" to="/add"><CirclePlus /> Add Recipe</Link>
                    <Link className="list-group-item list-group-item-action list-group-item-light p-3" to="/list"><List /> List Recipe</Link>
                    <Link className="list-group-item list-group-item-action list-group-item-light p-3" to="/orders"><ShoppingCart /> Orders</Link>
                </div>
            </div>
  )
}

export default Sidebar