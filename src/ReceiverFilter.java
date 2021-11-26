import com.intellij.psi.*;
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
        if (PsiUtils.isKotlin(element)){

            Log.d("shouldShow1"+element.getClass());
            if (element instanceof KtNameReferenceExpression) {
                KtNameReferenceExpression function = (KtNameReferenceExpression) element;

                Log.d("shouldShow2"+function.getReferencedName()+"/"+function.getText());
                Log.d("shouldShow3"+function.getParent().getClass());
                Log.d("shouldShow3"+function.getParent().getParent().getClass());
                Log.d("shouldShow3"+function.getParent().getParent().getParent().getClass());
                Log.d("shouldShow3"+function.getParent().getParent().getParent().getParent().getClass());
                PsiElement parent = function.getParent().getParent().getParent().getParent();
                if (parent instanceof KtCallExpression){
                    KtCallExpression aa= (KtCallExpression) parent;
                    Log.d("shouldShow4"+aa.getParent().getClass()+"/"+aa.getText());

                }

                if (parent instanceof KtParameterList){
                    KtParameterList aa= (KtParameterList) parent;
                    Log.d("shouldShow5"+aa.getParent().getClass()+"/"+aa.getText());
                    PsiElement parent1 = aa.getParent();
                    if (parent1 instanceof KtNamedFunction){
                        KtNamedFunction aa1= (KtNamedFunction) parent1;
                        Log.d("shouldShow7"+aa1.getParent().getClass()+"/"+aa1.getText()+"/"+aa1.getName());
                        if (aa1.getName()!=null){
                            if ((aa1.getName().equals("onEvent")
                                    || aa1.getName().equals("onEventMainThread")
                                    || aa1.getName().equals("onEventBackgroundThread")
                                    || aa1.getName().equals("onEventAsync"))) {
                                return true;
                            }
                        }
                    }
                }

                if (parent instanceof KtFile){
                    KtFile aa= (KtFile) parent;
                    Log.d("shouldShow6"+"/"+aa.getText());
                }
            }
        }else {
            if (element instanceof PsiJavaCodeReferenceElement) {
                if ((element = element.getParent()) instanceof PsiTypeElement) {
                    if ((element = element.getParent()) instanceof PsiParameter) {
                        if ((element = element.getParent()) instanceof PsiParameterList) {
                            if ((element = element.getParent()) instanceof PsiMethod) {
                                PsiMethod method = (PsiMethod) element;
                                Log.d("shouldShow5"+method);
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
