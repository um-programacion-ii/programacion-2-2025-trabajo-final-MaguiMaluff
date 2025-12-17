package um.backend.sales;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.backend.selection.SelectedSeat;
import um.backend.selection.SelectionService;
import um.backend.selection.SelectionStage;
import um.backend.selection.SelectionStateEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SalesService {

    private final SaleRepository saleRepo;
    private final SelectionService selectionService;

    public SalesService(SaleRepository saleRepo, SelectionService selectionService) {
        this.saleRepo = saleRepo;
        this.selectionService = selectionService;
    }

    @Transactional
    public SaleEntity createFromSelection(SelectionStateEntity state, String externalSaleId, BigDecimal total) {
        SaleEntity sale = new SaleEntity();
        sale.setId(UUID.randomUUID());
        sale.setUserId(state.getUserId());
        sale.setEventoId(state.getEventoId());
        sale.setTotalAmount(total);
        sale.setExternalSaleId(externalSaleId);

        List<SelectedSeat> seats = state.getSeats();
        List<String> names = state.getNames();

        for (int i = 0; i < seats.size(); i++) {
            SelectedSeat s = seats.get(i);
            String name = i < names.size() ? names.get(i) : null;
            SaleItemEntity item = new SaleItemEntity();
            item.setId(UUID.randomUUID());
            item.setSale(sale);
            item.setFila(s.fila);
            item.setColumna(s.columna);
            item.setNombre(name);
            sale.getItems().add(item);
        }

        SaleEntity saved = saleRepo.save(sale);

        // Resetear selección posterior a la venta
        state.setStage(SelectionStage.SELECTING);
        state.setBloqueadoHasta(null);
        state.setSeats(List.of());
        state.setNames(List.of());
        selectionService.updateAfterSale(state); // método helper para persistir cambios

        return saved;
    }

    public boolean isBlockedAndValid(SelectionStateEntity state, Instant now) {
        return state.getStage() == SelectionStage.BLOCKED
                && state.getBloqueadoHasta() != null
                && state.getBloqueadoHasta().isAfter(now);
    }
}