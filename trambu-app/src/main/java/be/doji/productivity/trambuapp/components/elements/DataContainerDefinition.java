package be.doji.productivity.trambuapp.components.elements;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataContainerDefinition<S, D> {

    private S dataContainer;
    private BiConsumer<S, D> addDataToield;
    private Function<S, D> getDataFromField;

    public DataContainerDefinition(S dataContainer, BiConsumer<S, D> dataSetter, Function<S, D> dataGetter) {
        this.dataContainer = dataContainer;
        this.addDataToield = dataSetter;
        this.getDataFromField = dataGetter;
    }

    public S getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(S dataContainer) {
        this.dataContainer = dataContainer;
    }

    public BiConsumer<S, D> getAddDataToield() {
        return addDataToield;
    }

    public void setAddDataToield(BiConsumer<S, D> addDataToield) {
        this.addDataToield = addDataToield;
    }

    public Function<S, D> getGetDataFromField() {
        return getDataFromField;
    }

    public void setGetDataFromField(Function<S, D> getDataFromField) {
        this.getDataFromField = getDataFromField;
    }

    public void setData(D data) {
        addDataToield.accept(this.dataContainer, data);
    }

    public D getData() {
        return getDataFromField.apply(dataContainer);
    }
}
