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
        if (isJava(psiElement)) {
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
        } else if (isKotlin(psiElement)) {
            if (psiElement instanceof KtDotQualifiedExpression) {
                KtDotQualifiedExpression all = (KtDotQualifiedExpression) psiElement;
                if (all.getFirstChild() instanceof KtDotQualifiedExpression && all.getLastChild() instanceof KtCallExpression) {
                    String start = all.getFirstChild().getText();
                    if (start != null && start.equals("CmpDispatcher.getInstance()")) {
                        KtCallExpression postRoot = (KtCallExpression) all.getLastChild();
                        if (postRoot.getFirstChild() instanceof KtNameReferenceExpression) {
                            KtNameReferenceExpression referenceExpression = (KtNameReferenceExpression) postRoot.getFirstChild();
                            if (referenceExpression.getReferencedName().equals("sendEvent")) {
                                return true;
                            }
                        }
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
                            (qualifiedName.equals("com.immomo.molive.common.component.common.evet.annotation.OnCmpEvent")
                                    || qualifiedName.equals("com.immomo.molive.common.component.common.call.annotation.OnCmpCall"))) {
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


}
