package be.doji.productivity.trambuapp.components.presenter;

import tornadofx.Component;

public abstract class Presenter extends Component {

  abstract void refresh();

  abstract void populate();
}
