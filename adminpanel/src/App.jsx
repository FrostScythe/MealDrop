import { Route, Routes } from 'react-router-dom'
import AddFood from './pages/AddFood'
import ListFood from './pages/ListFood'
import Sidebar from './components/Sidebar'
import Menubar from './components/Menubar'
import Orders from './pages/Orders'
import { useState } from 'react'

const App = () => {

    const [sidebarVisible, setSidebarVisible] = useState(true);

    const toggleSidebar = () => {
        setSidebarVisible(!sidebarVisible);
    }
  return (
    <div className="d-flex" id="wrapper">
           <Sidebar sidebarVisible={sidebarVisible} />
            <div id="page-content-wrapper">
                <Menubar toggleSidebar={toggleSidebar}/>
                <div className="container-fluid">
                    <Routes>
                        <Route path='/add' element={<AddFood />} />
                        <Route path='/list' element={<ListFood />} />
                        <Route path='/orders' element={<Orders />} />
                        <Route path='/' element={<ListFood />} />
                    </Routes>
                </div>
            </div>
        </div>
  )
}

export default App
