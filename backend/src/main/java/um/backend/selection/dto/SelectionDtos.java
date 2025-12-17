package um.backend.selection.dto;

import um.backend.selection.SelectedSeat;
import um.backend.selection.SelectionStage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SelectionDtos {

    public static class CreateRequest {
        public String userId;
        public long eventoId;
    }

    public static class SeatsRequest {
        public UUID selectionId;
        public List<SelectedSeat> seats;
    }

    public static class NamesRequest {
        public UUID selectionId;
        public List<String> names;
    }

    public static class BlockRequest {
        public UUID selectionId;
    }

    public static class SelectionResponse {
        public UUID id;
        public String userId;
        public long eventoId;
        public List<SelectedSeat> seats;
        public List<String> names;
        public SelectionStage stage;
        public Instant bloqueadoHasta;
        public Instant updatedAt;
    }
}