import axios from 'axios';
import React, { useEffect, useState } from 'react'
import RestService from '../../services/rest-service';
import { Link } from 'react-router-dom';
import vehicleCreateModal from './vehicleCreateModal';

function VehicleListCreate() {
    const [vehicleList, setvehicleList] = useState([]);

    useEffect(() => {
        RestService.getObjectList('/vehicle')
            .then((data) => {
                if (data != null){
                    setvehicleList(data);
                }
            })
    }, [])
    return (
        <>
            <div className="row col-9 justify-content-end mb-0 pb-0 pe-0">

                <a className="btn btn-primary col-auto rounded-0 rounded-top" data-bs-toggle="modal" data-bs-target="#createVehicleModal" href="#">Add Vehicle</a>

            </div>
            <div className="card row col-9 justify-content-center bg-light rounded-0 rounded-bottom rounded-start shadow-sm mt-0">
                <div className="card-title ps-2 mt-3">
                    <h4>All Vehicle</h4>
                </div>
                <div className="card-body px-4">
                    <table className="table table-striped table-hover">
                        <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Brand</th>
                            <th scope="col">Model</th>
                            <th scope="col"></th>
                            <th scope="col"></th>

                        </tr>
                        </thead>
                        <tbody>
                        {vehicleList.length !== 0 ? vehicleList.map((vehicle) => (
                            <tr>
                                <td>{vehicleList.id}</td>
                                <th>

                                </th>
                                <td>{vehicleList.brand}</td>
                                <td>{vehicleList.model}</td>
                                <td>{vehicleList.description}</td>
                                <td>
                                    <img src={`/api/vehicle/image/${vehicleList.id}`} alt="" style={{width: '100px', border: '1px solid lightgray'}} />
                                </td>
                                <td>
                                    <th scope="row">
                                        <Link to={`/vehicle/${vehicleList.id}`} className="link-secondary">
                                            View Details
                                        </Link>
                                    </th>
                                </td>
                            </tr>
                        )): (
                            <tr>No categories exist</tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
            <VehicleCreateModal />

        </>
    )
}
export default VehicleListCreate;