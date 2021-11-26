import com.intellij.psi.*;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import org.jetbrains.kotlin.psi.*;

import java.util.List;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class DispatchReceiverFilter implements Filter {
    private final String eventClass;
    String TAG = "shouldShow =";

    public DispatchReceiverFilter(String eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        if (element == null) return false;
        if (PsiUtils.isKotlin(element)) {
            Log.d(TAG + "1" + element.getClass());
//            PsiUtils.getpara(element);
            if (element.getParent() instanceof KtUserType) {
                if (element.getParent().getParent() instanceof KtNullableType) {
                    if (element.getParent().getParent().getParent() instanceof KtTypeReference) {
                        if (element.getParent().getParent().getParent().getParent() instanceof KtParameter) {
                            if (element.getParent().getParent().getParent().getParent().getParent() instanceof KtParameterList) {
                                if (element.getParent().getParent().getParent().getParent().getParent().getParent() instanceof KtNamedFunction) {
                                    if (checkATmethod(element)) return true;

                                }
                            }
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

    private boolean checkATmethod(PsiElement element) {
        KtNamedFunction parent = (KtNamedFunction) element.getParent().getParent().getParent().getParent().getParent().getParent();
        PsiElement[] children = parent.getChildren();
        PsiElement firstChild = parent.getFirstChild();
        Log.d(TAG + "7" + firstChild.getClass());
        Log.d(TAG + "8" + firstChild.getText());
        if (firstChild instanceof KtDeclarationModifierList) {
            if (firstChild.getText().equals("@OnCmpCall") || firstChild.getText().equals("@OnCmpEvent")) {
                for (int i = 0; i < children.length; i++) {
                    Log.d(TAG + "3" + children[i].getClass());
                    Log.d(TAG + "4" + children[i].getText());
                    if (children[i] instanceof KtParameterList) {
                        PsiElement child = children[i];
                        PsiElement[] children1 = child.getChildren();
                        for (int j = 0; j < children1.length; j++) {
                            Log.d(TAG + "5" + children1[j].getClass());
                            Log.d(TAG + "6" + children1[j].getText());

                            if (children1[j] instanceof KtParameter) {
                                PsiElement psiElement = children1[j];
                                PsiElement[] children2 = psiElement.getChildren();
                                for (int k = 0; k < children2.length; k++) {
                                    Log.d(TAG + "9" + children2[k].getClass());
                                    Log.d(TAG + "10" + children2[k].getText());
                                    if (children2[k] instanceof KtTypeReference) {
                                        KtTypeReference bb = (KtTypeReference) children2[k];
                                        if (bb.getText().contains("?")) {
                                            KtTypeElement typeElement = bb.getTypeElement();
                                            if (typeElement != null) {
                                                PsiElement[] children3 = typeElement.getChildren();
                                                for (int l = 0; l < children3.length; l++) {
                                                    if (children3[l] instanceof KtUserType) {
                                                        if (children3[l].getText().equals(eventClass)) {
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            if (bb.getText().equals(eventClass)) {
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
        return false;
    }
}
