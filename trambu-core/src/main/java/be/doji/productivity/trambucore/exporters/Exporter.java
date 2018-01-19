package be.doji.productivity.trambucore.exporters;

import java.io.IOException;

/**
 * @param <I> InputType
 * @param <O> OutputType
 *
 * @author Stijn Dejongh
 */
public interface Exporter<I, O> {

    O convert(I input) throws IOException;

}
