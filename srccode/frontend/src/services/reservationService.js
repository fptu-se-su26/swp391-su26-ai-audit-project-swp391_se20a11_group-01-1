import API from "./api";

export const getAllReservations = async () => {
  const response = await API.get("/reservations");
  return response.data;
};

export const createReservation = async (reservationData) => {
  const response = await API.post("/reservations", reservationData);
  return response.data;
};

export const updateReservationStatus = async (reservationId, status, assignedTable) => {
  const response = await API.put(`/reservations/${reservationId}/status`, {
    status,
    assignedTable
  });

  return response.data;
};
