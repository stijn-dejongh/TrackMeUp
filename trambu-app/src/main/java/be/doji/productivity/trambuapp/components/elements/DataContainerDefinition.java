package be.doji.productivity.trambuapp.components.elements;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataContainerDefinition<S, D> {

  private S dataContainer;
  private BiConsumer<S, D> dataSetter;
  private Function<S, D> dataGetter;

  public DataContainerDefinition(S dataContainer, BiConsumer<S, D> dataSetter,
      Function<S, D> dataGetter) {
    this.dataContainer = dataContainer;
    this.dataSetter = dataSetter;
    this.dataGetter = dataGetter;
  }

  public S getDisplayItem() {
    return dataContainer;
  }

  public D getData() {
    return dataGetter.apply(dataContainer);
  }

  public void setData(D data) {
    dataSetter.accept(this.dataContainer, data);
  }
}
