import com.intellij.psi.*;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import org.jetbrains.kotlin.psi.*;

import java.util.List;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class DispatchReceiverFilter implements Filter {
    private final PsiClass eventClass;
    String TAG = "shouldShow =";

    public DispatchReceiverFilter(PsiClass eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        if (element==null) return false;
        if (PsiUtils.isKotlin(element)){
            System.out.println(TAG+"1"+element.getClass());
            PsiUtils.getpara(element);
            if (element.getParent() instanceof KtUserType){
                if (element.getParent().getParent() instanceof KtNullableType){
                    if (element.getParent().getParent().getParent() instanceof KtTypeReference){
                        if (element.getParent().getParent().getParent().getParent() instanceof KtParameter){
                            if (element.getParent().getParent().getParent().getParent().getParent() instanceof KtParameterList){
                                if (element.getParent().getParent().getParent().getParent().getParent().getParent() instanceof KtNamedFunction){
                                    KtNamedFunction parent = (KtNamedFunction) element.getParent().getParent().getParent().getParent().getParent().getParent();
                                    PsiElement[] children = parent.getChildren();
                                    PsiElement firstChild = parent.getFirstChild();
                                    System.out.println(TAG+"7"+firstChild.getClass());
                                    System.out.println(TAG+"8"+firstChild.getText());
                                    if (firstChild instanceof KtDeclarationModifierList){
                                        if (firstChild.getText().equals("@OnCmpCall")||firstChild.getText().equals("@OnCmpEvent")){
                                            for (int i = 0; i < children.length; i++) {
                                                System.out.println(TAG+"3"+children[i].getClass());
                                                System.out.println(TAG+"4"+children[i].getText());
                                                if (children[i] instanceof KtParameterList){
                                                    PsiElement child = children[i];
                                                    PsiElement[] children1 = child.getChildren();
                                                    for (int j = 0; j < children1.length; j++) {
                                                        System.out.println(TAG+"5"+children1[j].getClass());
                                                        System.out.println(TAG+"6"+children1[j].getText());

                                                        if (children1[j] instanceof KtParameter){
                                                            PsiElement psiElement = children1[j];
                                                            PsiElement[] children2 = psiElement.getChildren();
                                                            for (int k = 0; k < children2.length; k++) {
                                                                System.out.println(TAG+"9"+children2[k].getClass());
                                                                System.out.println(TAG+"10"+children2[k].getText());
                                                                if (children2[k] instanceof KtTypeReference){
                                                                        KtTypeReference   bb = (KtTypeReference) children2[k];
                                                                        KtTypeElement typeElement = bb.getTypeElement();
                                                                        if (typeElement!=null){
                                                                            PsiElement[] children3 = typeElement.getChildren();
                                                                            for (int l = 0; l < children3.length; l++) {
                                                                                if (children3[l] instanceof KtUserType){
                                                                                    if (children3[l].getText().equals(eventClass.getName())){
                                                                                        return true;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }else {
            if (element instanceof PsiJavaCodeReferenceElement) {
                if ((element = element.getParent()) instanceof PsiTypeElement) {
                    if ((element = element.getParent()) instanceof PsiParameter) {
                        if ((element = element.getParent()) instanceof PsiParameterList) {
                            if ((element = element.getParent()) instanceof PsiMethod) {
                                PsiMethod method = (PsiMethod) element;
                                if (PsiUtils.isCmpSafeDispatcherReceiver(method)) {
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
