package um.backend.selection;

public enum SelectionStage {
    SELECTING,      // el usuario está eligiendo asientos
    FILLED_NAMES,   // ya cargó los nombres
    BLOCKED         // asientos bloqueados en la cátedra (con vencimiento)
}