import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import org.jetbrains.kotlin.psi.*;


/**
 * Created by kgmyshin on 2015/06/07.
 */
public class ReceiverFilter implements Filter {
    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        if (PsiUtils.isKotlin(element)) {

            Log.d("shouldShow1" + element.getClass());
//            PsiUtils.getpara(element);
            if (element instanceof KtNameReferenceExpression) {
                KtNameReferenceExpression function = (KtNameReferenceExpression) element;
                PsiElement parent = function.getParent();
                while (parent != null && !(parent instanceof KtNamedFunction)) {
                    parent = parent.getParent();
                }
                Log.d("shouldShow2" + element.getClass());

                if (parent !=null) {
                    KtNamedFunction aa1 = (KtNamedFunction) parent;
                    Log.d("shouldShow4" + aa1.getName());
                    if (aa1.getName() != null) {
                        if ((aa1.getName().equals("onEvent")
                                || aa1.getName().equals("onEventMainThread")
                                || aa1.getName().equals("onEventBackgroundThread")
                                || aa1.getName().equals("onEventAsync"))) {
                            return true;
                        }
                    }
                }
            }
        } else {
            if (element instanceof PsiJavaCodeReferenceElement) {
                if ((element = element.getParent()) instanceof PsiTypeElement) {
                    if ((element = element.getParent()) instanceof PsiParameter) {
                        if ((element = element.getParent()) instanceof PsiParameterList) {
                            if ((element = element.getParent()) instanceof PsiMethod) {
                                PsiMethod method = (PsiMethod) element;
                                Log.d("shouldShow5" + method);
                                if (PsiUtils.isEventBusReceiver(method)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
