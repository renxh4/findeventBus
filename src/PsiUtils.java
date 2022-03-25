import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.lang.Language;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.psi.*;
import org.jetbrains.kotlin.psi.*;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class PsiUtils {

    private static int mcount = 0;

    public static PsiClass getClass(PsiType psiType, PsiElement context) {
        if (psiType instanceof PsiClassType) {
            return ((PsiClassType) psiType).resolve();
        } else if (psiType instanceof PsiPrimitiveType) {
            PsiClassType wrapperType = ((PsiPrimitiveType) psiType).getBoxedType(context);
            return wrapperType == null ? null : wrapperType.resolve();
        }
        return null;
    }

    public static boolean isEventBusReceiver(PsiElement psiElement) {
        if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            if (method.getName() != null
                    && (method.getName().equals("onEvent")
                    || method.getName().equals("onEventMainThread")
                    || method.getName().equals("onEventBackgroundThread")
                    || method.getName().equals("onEventAsync"))
                    && method.getParameterList().getParametersCount() == 1
                    && method.getParameterList().getParameters()[0].getType() instanceof PsiClassType) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEventBusPost(PsiElement psiElement) {
        if (psiElement instanceof PsiCallExpression) {
            PsiCallExpression callExpression = (PsiCallExpression) psiElement;
            PsiMethod method = callExpression.resolveMethod();
            if (method != null) {
                String name = method.getName();
                PsiElement parent = method.getParent();
                if (name != null && name.equals("dispatch") && parent instanceof PsiClass) {
                    PsiClass implClass = (PsiClass) parent;
                    if (isEventBusClass(implClass) || isSuperClassEventBus(implClass)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean isCmpSafeDispatcherReceiver(PsiElement psiElement) {
        if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            PsiAnnotation[] annotations = method.getAnnotations();
            if (annotations.length > 0) {
                for (PsiAnnotation annotation : annotations) {
                    String qualifiedName = annotation.getQualifiedName();
                    if (qualifiedName != null && method.getParameterList().getParametersCount() == 1
                            && method.getParameterList().getParameters()[0].getType() instanceof PsiClassType &&
                            (qualifiedName.equals("common.component.common.evet.annotation.OnCmpEvent")
                                    || qualifiedName.equals("common.component.common.call.annotation.OnCmpCall"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean isCmpSafeDispatcherPost(PsiElement psiElement) {
        if (psiElement instanceof PsiCallExpression) {
            PsiCallExpression callExpression = (PsiCallExpression) psiElement;
            PsiMethod method = callExpression.resolveMethod();
            if (method != null) {
                String name = method.getName();
                PsiElement parent = method.getParent();
                if (name != null && parent instanceof PsiClass && (name.equals("sendEvent") || name.equals("sendCall"))) {
                    PsiClass implClass = (PsiClass) parent;
                    if (isCmpSafeDispatcherClass(implClass) || isSuperClassCmpSafeDispatcher(implClass)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private static boolean isEventBusClass(PsiClass psiClass) {
        return psiClass.getName().equals("NotifyDispatcher");
    }

    private static boolean isSuperClassEventBus(PsiClass psiClass) {
        PsiClass[] supers = psiClass.getSupers();
        if (supers.length == 0) {
            return false;
        }
        for (PsiClass superClass : supers) {
            if (superClass.getName().equals("NotifyDispatcher")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCmpSafeDispatcherClass(PsiClass psiClass) {
        return psiClass.getName().equals("CmpSafeDispatcher") || psiClass.getName().equals("CmpDispatcher");
    }

    private static boolean isSuperClassCmpSafeDispatcher(PsiClass psiClass) {
        PsiClass[] supers = psiClass.getSupers();
        if (supers.length == 0) {
            return false;
        }
        for (PsiClass superClass : supers) {
            if (superClass.getName().equals("CmpSafeDispatcher") || superClass.getName().equals("CmpDispatcher")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKotlin(PsiElement psiElement) {
        return psiElement.getLanguage().is(Language.findLanguageByID("kotlin"));
    }

    public static boolean isJava(PsiElement psiElement) {
        return psiElement.getLanguage().is(Language.findLanguageByID("JAVA"));
    }

    public static boolean checkIsKotlinInstalled() {
        PluginId pluginId = PluginId.findId("org.jetbrains.kotlin");
        if (pluginId != null) {
            IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
            return pluginDescriptor != null && pluginDescriptor.isEnabled();
        }
        return false;
    }

    public static void getpara(PsiElement element) {
        Log.d("开始-----------------");
        PsiElement element1 = element;
        while (element1.getParent() != null) {
            element1 = element1.getParent();
            Log.d("SenderFilter1 = " + element1.getClass() + "/");
            Log.d("SenderFilter2 = " + element1.getText() + "/");
        }
        Log.d("结束-----------------");
    }


    public static void getchild(PsiElement element, int count, String tag) {
        mcount = count;
        Log.d("开始-----------------" + (element == null) + "/" + "/" + (element.getChildren().length <= 0) + "/" + (mcount > 100));
        if (element == null || element.getChildren().length <= 0 || mcount > 100) return;
        Log.d("开始-----------------");
        if (element.getChildren().length > 0) {
            PsiElement[] children = element.getChildren();
            for (PsiElement child : children) {
                Log.d(tag + child.getClass() + "/");
                Log.d(tag + child.getText() + "/");
                getchild(element, mcount++, tag);
            }
        }
        Log.d("结束-----------------");
    }

    public static boolean isEventBusReceiverKT(PsiElement element, String TAG) {
        if (element instanceof KtNamedFunction) {
            KtNamedFunction parent = (KtNamedFunction) element;
            PsiElement firstChild = parent.getFirstChild();
            Log.d(TAG + "1" + firstChild.getClass());
            Log.d(TAG + "2" + firstChild.getText());
            if (firstChild instanceof KtDeclarationModifierList) {
                if (firstChild.getText().equals("@OnCmpCall") || firstChild.getText().equals("@OnCmpEvent")) {
                    Log.d(TAG + "3" + firstChild.getText());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isEventBusReceiverKTNotifyDispatcher(PsiElement element, String TAG) {
        if (element instanceof KtNamedFunction) {
            KtNamedFunction parent = (KtNamedFunction) element;
            PsiElement firstChild = parent.getFirstChild();
            Log.d(TAG + "1" + firstChild.getClass());
            Log.d(TAG + "2" + firstChild.getText());
            Log.d(TAG + "9" + parent.getName());
            if (parent.getName() != null && (parent.getName().equals("onEvent")
                    || parent.getName().equals("onEventMainThread")
                    || parent.getName().equals("onEventBackgroundThread")
                    || parent.getName().equals("onEventAsync"))) {
                Log.d(TAG + "4" + parent.getName());
                return true;
            }
        }
        return false;
    }

    public static boolean isEventBusPostKT(PsiElement psiElement, String TAG) {
        if (psiElement instanceof KtCallExpression) {
            if (psiElement.getParent() instanceof KtDotQualifiedExpression) {
                PsiElement firstChild1 = psiElement.getParent().getFirstChild();
                Log.d(TAG + "1" + firstChild1.getText() + "/" + firstChild1.getClass());
                if (firstChild1 instanceof KtDotQualifiedExpression) {
                    if (firstChild1.getText().equals("CmpDispatcher.getInstance()")) {
                        PsiElement lastChild = psiElement.getParent().getLastChild();
                        if (lastChild instanceof KtCallExpression) {
                            KtCallExpression aa3 = (KtCallExpression) lastChild;
                            PsiElement firstChild2 = aa3.getFirstChild();
                            if (firstChild2 instanceof KtNameReferenceExpression) {
                                String text = firstChild2.getText();
                                Log.d(TAG + "2" + text + "/");
                                if (text.equals("sendEvent") || text.equals("sendCall")) {
                                    return true;
                                }
                            }
                        }
                    }
                }

                if (firstChild1 instanceof KtNameReferenceExpression) {
                    Log.d(TAG + "3" + firstChild1.getText() + "/");
                    //适配NotifyDispatcher
                    if (!firstChild1.getText().equals("NotifyDispatcher")) {
                        PsiElement lastChild = psiElement.getParent().getLastChild();
                        if (lastChild instanceof KtCallExpression) {
                            KtCallExpression aa3 = (KtCallExpression) lastChild;
                            PsiElement firstChild2 = aa3.getFirstChild();
                            if (firstChild2 instanceof KtNameReferenceExpression) {
                                String text = firstChild2.getText();
                                Log.d(TAG + "7" + text + "/");
                                if (text.equals("sendEvent") || text.equals("sendCall")) {
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

    public static boolean isEventBusPostKTNotifyDispatcher(PsiElement psiElement, String TAG) {
        if (psiElement instanceof KtCallExpression) {
            if (psiElement.getParent() instanceof KtDotQualifiedExpression) {
                PsiElement firstChild1 = psiElement.getParent().getFirstChild();
                Log.d(TAG + "1" + firstChild1.getText() + "/" + firstChild1.getClass());

                if (firstChild1 instanceof KtNameReferenceExpression) {
                    Log.d(TAG + "3" + firstChild1.getText() + "/");
                    //适配NotifyDispatcher
                    if (firstChild1.getText().equals("NotifyDispatcher")) {
                        PsiElement[] children = psiElement.getParent().getChildren();
                        for (int i = 0; i < children.length; i++) {
                            Log.d(TAG + "4" + children[i].getClass() + "/");
                            Log.d(TAG + "5" + children[i].getText() + "/");

                            if (children[i] instanceof KtCallExpression) {
                                PsiElement firstChild = children[i].getFirstChild();
                                Log.d(TAG + "6" + firstChild.getText() + "/");
                                if (firstChild.getText().equals("dispatch")) {
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
