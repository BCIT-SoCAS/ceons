package mtk.eon.io;

import java.io.IOException;

import javafx.stage.FileChooser.ExtensionFilter;

public interface FileFormat2<R, P> {
	
	public abstract ExtensionFilter getExtensionFilter();
	
	public abstract R load(File file, P parameter) throws IOException;
}
