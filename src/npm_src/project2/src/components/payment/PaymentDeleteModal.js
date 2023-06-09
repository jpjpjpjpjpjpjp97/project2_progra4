import React, {useState} from 'react'
import RestService from '../../services/rest-service'

function PaymentDeleteModal({ paymentData}) {
    const [createErrors, setCreateErrors] = useState([]);
    const [error, setError] = useState("");
    const deletePayment = (event) => {
        event.preventDefault();
        RestService.deleteObject(`payment/${paymentData.id}/delete`)
            .then((data) => {
                console.log(data);
                window.location.replace('/payment/');
            })
            .catch((data) => {
                if (Array.isArray(data)){
                    const errors = data.map((error) => ({field: error.field, message: error.defaultMessage}));
                    setCreateErrors(errors);
                }
                else{
                    setError(data);
                }
            })
    }
    return (
        <div className="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title" id="deleteModalLabel">Delete Payment</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div className="modal-body">
                        <h5>Are you sure you want to delete this payment?</h5>
                        {error && (
                            <ul class="alert alert-danger ps-4">
                                <li>{error}</li>
                            </ul>
                        )}
                    </div>
                    <div className="modal-footer">
                        <form onSubmit={deletePayment}>
                            <button type="submit" className="btn btn-danger">Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default PaymentDeleteModal