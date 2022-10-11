package com.cdek.storage.buffer.refresher.helper;

import com.cdek.storage.buffer.ports.output.PackageBufferRepository;
import com.cdek.storage.utils.OrderTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class PackageComparerHelperTest {

    @MockBean
    PackageBufferRepository packageBufferRepository;

    PackageComparerHelper packageComparerHelper;

    @BeforeEach
    void before() {
        packageComparerHelper = new PackageComparerHelper(packageBufferRepository);
        Mockito.clearInvocations(packageBufferRepository);
    }

    @Test
    void comparePackages_NewPackagesListEqualsOldPackagesList() {
        Mockito.doNothing().when(packageBufferRepository).saveOrUpdatePackage(Mockito.any());
        Mockito.doNothing().when(packageBufferRepository).setPackagesIsDeleted(Mockito.anyList());

        packageComparerHelper.comparePackages(OrderTestUtils.getOldPackageUuidList(), OrderTestUtils.getPackageList());

        Mockito.verify(packageBufferRepository, Mockito.times(2)).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(packageBufferRepository, Mockito.never()).setPackagesIsDeleted(Mockito.anyList());
    }

    @Test
    void comparePackages_NewPackagesListHaveOneNewPackage() {
        Mockito.doNothing().when(packageBufferRepository).saveOrUpdatePackage(Mockito.any());
        Mockito.doNothing().when(packageBufferRepository).setPackagesIsDeleted(Mockito.anyList());

        packageComparerHelper
                .comparePackages(OrderTestUtils.getOldPackageUuidList(), OrderTestUtils.getListWithThreePackages());

        Mockito.verify(packageBufferRepository, Mockito.times(3)).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(packageBufferRepository, Mockito.never()).setPackagesIsDeleted(Mockito.anyList());
    }

    @Test
    void comparePackages_NewPackagesListNotHaveOnePackage() {
        Mockito.doNothing().when(packageBufferRepository).saveOrUpdatePackage(Mockito.any());
        Mockito.doNothing().when(packageBufferRepository).setPackagesIsDeleted(Mockito.anyList());

        packageComparerHelper.comparePackages(OrderTestUtils.getOldPackageUuidList(),
                Collections.singletonList(OrderTestUtils.createPackage1Model()));

        Mockito.verify(packageBufferRepository, Mockito.times(1)).saveOrUpdatePackage(Mockito.any());
        Mockito.verify(packageBufferRepository, Mockito.times(1)).setPackagesIsDeleted(Mockito.anyList());
    }
}
