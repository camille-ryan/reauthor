import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;

/**
 * Created by Ryan Brady on 7/16/16.
 */
public class Reauthor {
    public static void main(String[] args) throws Exception {
        WordVectorSerializer.loadFullModel("fittedmodel/");
    }
}
