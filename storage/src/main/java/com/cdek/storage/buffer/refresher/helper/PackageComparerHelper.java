package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.buffer.ports.output.PackageBufferRepository;
import com.cdek.storage.model.order.Package;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для сравнения пакетов: сравнивает список старых пакетов (которые лежат в БД) со списком новых,
 * которые пришли с шины.
 */
@Component
@RequiredArgsConstructor
public class PackageComparerHelper {

    private final PackageBufferRepository packageBufferRepository;

    /**
     * <p>1) Берем список новых пакетов <code>newPackList</code> и сохраняем/обновляем все пакеты из этого списка.</p>
     * <p>2) Создаем список <code>newPackageUuidList</code> состоящий из идентификаторов пакетов,
     * которые взяли из списка <code>newPackList</code>.</p>
     * <p>3) Создаем новый список <code>packageListToDelete</code>, состоящий из идентификаторов пакетов,
     * которых нет в новом списке <code>newPackageUuidList</code>, и удаляем эти пакеты.</p>
     *
     * @param oldPackUuidList список идентификаторов пакетов уже хранящихся в бд.
     * @param newPackList   список новый пакетов полученных с шины.
     */
    public void comparePackages(@Nonnull List<String> oldPackUuidList, @Nonnull List<Package> newPackList) {

        newPackList.forEach(packageBufferRepository::saveOrUpdatePackage);

        final List<String> newPackageUuidList = newPackList.stream()
                .map(Package::getPackageUuid)
                .collect(Collectors.toList());

        final List<String> packageListToDelete = oldPackUuidList.stream()
                .filter(sellerId -> !newPackageUuidList.contains(sellerId))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(packageListToDelete)) {
            packageBufferRepository.setPackagesIsDeleted(packageListToDelete);
        }
    }
}
