import com.intellij.psi.*;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import org.jetbrains.kotlin.psi.*;
import sun.rmi.runtime.Log;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class ReceiverFilter implements Filter {
    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        if (PsiUtils.isKotlin(element)){

            System.out.println("shouldShow1"+element.getClass());
            if (element instanceof KtNameReferenceExpression) {
                KtNameReferenceExpression function = (KtNameReferenceExpression) element;

                System.out.println("shouldShow2"+function.getReferencedName()+"/"+function.getText());
                System.out.println("shouldShow3"+function.getParent().getClass());
                System.out.println("shouldShow3"+function.getParent().getParent().getClass());
                System.out.println("shouldShow3"+function.getParent().getParent().getParent().getClass());
                System.out.println("shouldShow3"+function.getParent().getParent().getParent().getParent().getClass());
                PsiElement parent = function.getParent().getParent().getParent().getParent();
                if (parent instanceof KtCallExpression){
                    KtCallExpression aa= (KtCallExpression) parent;
                    System.out.println("shouldShow4"+aa.getParent().getClass()+"/"+aa.getText());

                }

                if (parent instanceof KtParameterList){
                    KtParameterList aa= (KtParameterList) parent;
                    System.out.println("shouldShow5"+aa.getParent().getClass()+"/"+aa.getText());
                    PsiElement parent1 = aa.getParent();
                    if (parent1 instanceof KtNamedFunction){
                        KtNamedFunction aa1= (KtNamedFunction) parent1;
                        System.out.println("shouldShow7"+aa1.getParent().getClass()+"/"+aa1.getText()+"/"+aa1.getName());
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
                    System.out.println("shouldShow6"+"/"+aa.getText());
                }
            }
        }else {
            if (element instanceof PsiJavaCodeReferenceElement) {
                if ((element = element.getParent()) instanceof PsiTypeElement) {
                    if ((element = element.getParent()) instanceof PsiParameter) {
                        if ((element = element.getParent()) instanceof PsiParameterList) {
                            if ((element = element.getParent()) instanceof PsiMethod) {
                                PsiMethod method = (PsiMethod) element;
                                System.out.println("shouldShow5"+method);
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

    private static boolean safeEquals(String obj, String value) {
        return obj != null && obj.equals(value);
    }
}
