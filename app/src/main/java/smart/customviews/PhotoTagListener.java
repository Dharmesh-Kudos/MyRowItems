package smart.customviews;

/**
 * This Interface Contains All Method Related To PhotoTagListener.
 *
 * @author tasol
 */
public interface PhotoTagListener {

    void onTagedItemClicked(int position, Object data);

    void onAddNewTag(String rectPosition);

    void showTagOptions(boolean isTagCanceld);

    void onTagAreaConflict();

    void onCancel();

}
