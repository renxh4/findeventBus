import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;
import org.intellij.plugins.relaxNG.compact.psi.util.PsiFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class HaniEventBusLineMarkerProvider implements LineMarkerProvider {

    public static final Icon ICON = IconLoader.getIcon("/icons/icon_too.png");
    public static final Icon ICON_SUN = IconLoader.findIcon("/icons/icon_sun.png");

    public static final int MAX_USAGES = 100;

    private static GutterIconNavigationHandler<PsiElement> SHOW_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethod) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass("com.immomo.molive.foundation.eventcenter.eventdispatcher.NotifyDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod = eventBusClass.findMethodsByName("dispatch", false)[0];
                        PsiMethod method = (PsiMethod) psiElement;
                        PsiClass eventClass = ((PsiClassType) method.getParameterList().getParameters()[0].getTypeElement().getType()).resolve();
                        new ShowUsagesAction(new SenderFilter(eventClass.getName())).startFindUsages(null, postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                    }
                }
            };


    private static GutterIconNavigationHandler<PsiElement> SHOW_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression expression = (PsiMethodCallExpression) psiElement;
                        PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
                        if (expressionTypes.length > 0) {
                            PsiClass eventClass = PsiUtils.getClass(expressionTypes[0], psiElement);
                            if (eventClass != null) {
                                new ShowUsagesAction(new ReceiverFilter()).startFindUsages(null, eventClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                            }
                        }
                    }
                }
            };


    private static GutterIconNavigationHandler<PsiElement> SHOW_CmpSafeDispatcher_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethod) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass("com.immomo.molive.common.component.common.dispatcher.CmpSafeDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod = eventBusClass.findMethodsByName("sendEvent", false)[0];
                        PsiMethod postMethod1 = eventBusClass.findMethodsByName("sendCall", false)[0];

                        PsiClass eventBusClass1 = javaPsiFacade.findClass("com.immomo.molive.common.component.common.dispatcher.CmpDispatcher", GlobalSearchScope.allScope(project));
                        PsiMethod postMethod2 = eventBusClass1.findMethodsByName("sendEvent", false)[0];
                        PsiMethod postMethod3 = eventBusClass1.findMethodsByName("sendCall", false)[0];
                        ArrayList<PsiElement> psiMethodArrayList = new ArrayList<>();
                        psiMethodArrayList.add(postMethod);
                        psiMethodArrayList.add(postMethod1);
                        psiMethodArrayList.add(postMethod2);
                        psiMethodArrayList.add(postMethod3);
                        Log.d("发送方法" + postMethod);
                        PsiMethod method = (PsiMethod) psiElement;
                        PsiClass eventClass = ((PsiClassType) method.getParameterList().getParameters()[0].getTypeElement().getType()).resolve();
                        Log.d("发送类" + eventClass);
                        new ShowUsagesAction(new SenderFilter(eventClass.getName())).startFindUsages(psiMethodArrayList, postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                    }
                }
            };

    private static GutterIconNavigationHandler<PsiElement> SHOW_CmpSafeDispatcher_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression expression = (PsiMethodCallExpression) psiElement;
                        Log.d("接收1" + expression);
                        PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
                        if (expressionTypes.length > 0) {
                            Log.d("接收2" + expressionTypes[0]);
                            Log.d("接收3" + psiElement);
                            PsiClass eventClass = PsiUtils.getClass(expressionTypes[0], psiElement);
                            Log.d("接收4" + eventClass);
                            if (eventClass != null) {
                                new ShowUsagesAction(new DispatchReceiverFilter(eventClass.getName())).startFindUsages(null, eventClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                            }
                        }
                    }
                }
            };


    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (!PsiUtils.isJava(psiElement)) return null;
        if (PsiUtils.isEventBusPost(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusReceiver(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_SENDERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isCmpSafeDispatcherPost(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_CmpSafeDispatcher_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isCmpSafeDispatcherReceiver(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    null, SHOW_CmpSafeDispatcher_SENDERS,
                    GutterIconRenderer.Alignment.LEFT);
        }

        return null;
    }

}
