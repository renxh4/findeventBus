import com.intellij.psi.PsiElement;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class ReceiverFilterKotlin implements Filter {
    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        System.out.println("ReceiverFilterKotlin 0 "+ PsiUtils.isKotlin(element));
        System.out.println(("ReceiverFilterKotlin 0 " + element.toString()));
        if (PsiUtils.isEventBusReceiver(element)) {
            System.out.println("ReceiverFilterKotlin 1 isEventBusReceiver");
            return true;
        }
        return false;
    }
}
