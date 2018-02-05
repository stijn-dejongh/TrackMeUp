package be.doji.productivity.trambuapp.components.elements;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataContainerDefinition<S, D> {

  private S dataContainer;
  private BiConsumer<S, D> setDataToField;
  private Function<S, D> getDataFromField;

  public DataContainerDefinition(S dataContainer, BiConsumer<S, D> dataSetter,
      Function<S, D> dataGetter) {
    this.dataContainer = dataContainer;
    this.setDataToField = dataSetter;
    this.getDataFromField = dataGetter;
  }

  public S getDisplayItem() {
    return dataContainer;
  }

  public void setDataContainer(S dataContainer) {
    this.dataContainer = dataContainer;
  }

  public BiConsumer<S, D> getSetDataToField() {
    return setDataToField;
  }

  public void setSetDataToField(BiConsumer<S, D> setDataToField) {
    this.setDataToField = setDataToField;
  }

  public Function<S, D> getGetDataFromField() {
    return getDataFromField;
  }

  public void setGetDataFromField(Function<S, D> getDataFromField) {
    this.getDataFromField = getDataFromField;
  }

  public D getData() {
    return getDataFromField.apply(dataContainer);
  }

  public void setData(D data) {
    setDataToField.accept(this.dataContainer, data);
  }
}
